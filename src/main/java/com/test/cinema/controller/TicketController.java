package com.test.cinema.controller;

import com.test.cinema.model.entity.*;
import com.test.cinema.model.request.TicketRequestBean;
import com.test.cinema.model.response.TicketResponseBean;
import com.test.cinema.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.test.cinema.util.Constant.*;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketService service;


    @RequestMapping(method = RequestMethod.GET)
    public List<TicketResponseBean> getList(@RequestParam(name = FILTER_NAME_PAGE, defaultValue = FILTER_NAME_PAGE_DEFAULT_VALUE) Integer page,
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

        Map<String, Object> filter = new HashMap<>();
        Page<Ticket> tickets = service.getList(filter, page - 1, perPage);


        response.setHeader(HEADER_NAME_PAGES, String.valueOf(tickets.getTotalPages()));
        response.setHeader(HEADER_NAME_PAGE, String.valueOf(page));
        response.setHeader(HEADER_NAME_PAGE_SIZE, String.valueOf(perPage));
        return tickets.getContent().stream().map(this::entityToBean).collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public TicketResponseBean getById(@PathVariable("id") Integer id){
        return entityToBean(service.getById(id));
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponseBean create(@RequestBody TicketRequestBean bean) {
        Ticket ticket = beanToEntity(bean);
        ticket = service.create(ticket);
        return entityToBean(ticket);
    }


    private Ticket beanToEntity(TicketRequestBean bean) {
        Ticket entity = new Ticket();
        entity.setId(bean.getId());

        Seance seance = new Seance();
        seance.setId(bean.getSeanceId());
        entity.setSeance(seance);

        Place place = new Place();
        place.setId(bean.getPlaceId());
        entity.setPlace(place);

        Order order = new Order();
        order.setId(bean.getOrderId());
        entity.setOrder(order);
        return entity;
    }

    private TicketResponseBean entityToBean(Ticket entity) {
        TicketResponseBean bean = new TicketResponseBean();
        bean.setId(entity.getId());
        bean.setCreatedAt(entity.getCreatedAt());
        bean.setCode(entity.getCode());
        bean.setSeanceId(entity.getSeance().getId());
        bean.setPlaceId(entity.getPlace().getId());
        if (entity.getOrder() != null) {
            bean.setOrderId(entity.getOrder().getId());
        } else {
            bean.setOrderId(null);
        }
        return bean;
    }
}
