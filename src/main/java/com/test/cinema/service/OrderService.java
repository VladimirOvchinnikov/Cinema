package com.test.cinema.service;

import com.test.cinema.exception.RestException;
import com.test.cinema.model.entity.Order;
import com.test.cinema.model.entity.Ticket;
import com.test.cinema.model.entity.User;
import com.test.cinema.repository.OrderRepository;
import com.test.cinema.service.util.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private MessageService messageService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private SeanceService seanceService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository repository;


    public Page<Order> getList(Map<String, Object> filter, Integer page, Integer perPage) {
        Page<Order> orders;
        if (filter.get("id") != null) {
            Order o = new Order();
            o.setId((Integer) filter.get("id"));
            orders = repository.findAll(Example.of(o), PageRequest.of(page, perPage));
        } else {
            orders = repository.findAll(PageRequest.of(page, perPage, new Sort(Sort.Direction.ASC, "createdAt")));
        }
        return orders;
    }

    public Order getById(Integer id) {
        Map<String, Object> filter = new HashMap<>();
        filter.putIfAbsent("id", id);
        List<Order> orders = getList(filter, 0, 1).getContent();
        return CollectionUtils.isEmpty(orders) ? null : orders.get(0);
    }

    @Transactional
    public Order create(Order order) {
        validate(order);
        order = repository.saveAndFlush(order);
        ticketService.buyTickets(order.getTickets(), order);
        return order;
    }

    private void validate(Order order) {
        if (order.getUser().getId() == null) {
            throw new RestException(messageService.getMessage("order.error.not-found.user"));
        }
        User user = userService.getById(order.getUser().getId());
        if(user == null){
            throw new RestException(messageService.getMessage("order.error.not-found.user"));
        }
        if (CollectionUtils.isEmpty(order.getTickets())) {
            throw new RestException(messageService.getMessage("order.error.invalid.zero-ticket"));
        }

        List<Ticket> tickets = new ArrayList<>();
        order.getTickets().forEach(t -> {
            if (t == null || t.getId()==null) {
                throw new RestException(messageService.getMessage("order.error.not-found.ticket"));
            }
            Ticket ticket = ticketService.getById(t.getId());
            if (ticket == null) {
                throw new RestException(messageService.getMessage("order.error.not-found.ticket"));
            }
            if (ticket.getOrder() != null && !ticket.getOrder().getId().equals(order.getId())) {
                throw new RestException(messageService.getMessage("order.error.invalid.ticket"));
            }
            tickets.add(ticket);
        });
//        order.setTickets(tickets);
    }

    @Transactional
    public Order update(Order order) {
        validate(order);
        Order instance = getById(order.getId());
        if (instance == null) {
            throw new RestException(messageService.getMessage("order.error.not-found", order.getId()));
        }


        Order finalOrder = order;
        List<Ticket> cancelTickets = instance.getTickets().stream().filter(t -> !finalOrder.getTickets().contains(t)).collect(Collectors.toList());
        List<Ticket> buyTickets = order.getTickets().stream().filter(t -> !instance.getTickets().contains(t)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(buyTickets)) {
            ticketService.buyTickets(buyTickets, order);
        }
        if (!CollectionUtils.isEmpty(cancelTickets)) {
            ticketService.cancelTickets(cancelTickets, order);
        }

        order.setCreatedAt(instance.getCreatedAt());
        order = repository.saveAndFlush(order);
        return order;
    }

    @Transactional
    public void delete(Integer id) {
        checkNotCancelOrder(id);
        ticketService.cancelTicketsByOrder(id);
        repository.delete(id);
    }

    private void checkNotCancelOrder(Integer id) {
        if (ticketService.checkNotCancelOrder(id)){
            throw new RestException(messageService.getMessage("order.error.invalid.not-delete", id));
        }
    }
}
