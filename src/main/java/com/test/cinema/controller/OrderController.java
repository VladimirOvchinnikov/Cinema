package com.test.cinema.controller;


import com.test.cinema.model.entity.BaseEntity;
import com.test.cinema.model.entity.Order;
import com.test.cinema.model.entity.Ticket;
import com.test.cinema.model.entity.User;
import com.test.cinema.model.request.OrderRequestBean;
import com.test.cinema.model.response.OrderResponseBean;
import com.test.cinema.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.test.cinema.util.Constant.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<OrderResponseBean> getList(@RequestParam(name = FILTER_NAME_PAGE, defaultValue = FILTER_NAME_PAGE_DEFAULT_VALUE) Integer page,
                          @RequestParam(name = FILTER_NAME_PER_PAGE, defaultValue = FILTER_NAME_PER_PAGE_DEFAULT_VALUE) Integer perPage,
                          HttpServletResponse response) {
        if (page < DEFAULT_MIN_PAGE) {
            page = DEFAULT_MIN_PAGE;
        }

        if (perPage < 0) {
            perPage = DEFAULT_PAGE_SIZE;
        } else if (perPage > DEFAULT_MAX_PAGE_SIZE) {
            perPage = DEFAULT_MAX_PAGE_SIZE;
        }

        Map<String,Object> filter = new HashMap<>();
        Page<Order> orders = service.getList(filter, page-1, perPage);

        response.setHeader(HEADER_NAME_PAGES, String.valueOf(orders.getTotalPages()));
        response.setHeader(HEADER_NAME_PAGE, String.valueOf(page));
        response.setHeader(HEADER_NAME_PAGE_SIZE, String.valueOf(perPage));
        return orders.stream().map(this::entityToBean).collect(Collectors.toList());
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public OrderResponseBean getById(@PathVariable("id") Integer id) {
        Order order = service.getById(id);
        if (order == null) {
            return null;
        }
        return entityToBean(order);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseBean create(@RequestBody OrderRequestBean bean){
        Order order = beanToEntity(bean);
        order = service.create(order);
        return entityToBean(order);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public OrderResponseBean update(@PathVariable("id") Integer id,
                                     @RequestBody OrderRequestBean bean){
        bean.setId(id);
        Order order = beanToEntity(bean);
        order = service.update(order);
        return entityToBean(order);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Integer id){
        service.delete(id);
    }


    private Order beanToEntity(OrderRequestBean bean) {
        Order entity = new Order();
        entity.setId(bean.getId());
        entity.setBonusCount(bean.getBonusCount());
        entity.setTicketCount(bean.getTicketCount());
        entity.setTotalPrice(bean.getTotalPrice());

        User user = new User();
        user.setId(bean.getUserId());
        entity.setUser(user);

        List<Ticket> tickets = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bean.getTicketIds())) {
            bean.getTicketIds().forEach(tId -> {
                Ticket ticket = new Ticket();
                ticket.setId(tId);
                tickets.add(ticket);
            });
        }
        entity.setTickets(tickets);
        return entity;
    }

    private OrderResponseBean entityToBean(Order entity) {
        OrderResponseBean bean = new OrderResponseBean();
        bean.setId(entity.getId());
        bean.setBonusCount(entity.getBonusCount());
        bean.setCreatedAt(entity.getCreatedAt());
        bean.setTicketCount(entity.getTicketCount());
        bean.setTotalPrice(entity.getTotalPrice());
        bean.setUserId(entity.getUser().getId());
        bean.setTicketIds(entity.getTickets().stream().map(BaseEntity::getId).collect(Collectors.toList()));
        return bean;
    }

}
