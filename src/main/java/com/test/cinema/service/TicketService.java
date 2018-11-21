package com.test.cinema.service;

import com.test.cinema.exception.RestException;
import com.test.cinema.model.entity.*;
import com.test.cinema.repository.TicketRepository;
import com.test.cinema.service.util.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class TicketService {

    @Autowired
    private TicketRepository repository;

    @Autowired
    private HallService hallService;

    @Autowired
    private SeanceService seanceService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private MessageService messageService;

    public Ticket create(Ticket ticket) {
        validate(ticket);
        if (ticket.getOrder().getId() == null) {
            ticket.setOrder(null);
        }
        ticket = repository.saveAndFlush(ticket);
        return ticket;
    }

    private Boolean checkCountPlaceInHall(Ticket ticket) {
        Integer countPlace = 0;
        List<Row> rows = hallService.loadRows(ticket.getSeance().getHall());
        for (Row row : rows) {
            countPlace += row.getPlaces().size();
        }
        Integer countTicket = repository.getCountTicketBySeance(ticket.getSeance());
        return countTicket < countPlace;
    }

    private void validate(Ticket ticket) {
        if (ticket.getSeance().getId() == null) {
            throw new RestException(messageService.getMessage("ticket.error.not-found.seance"));
        }
        Seance seance = seanceService.getById(ticket.getSeance().getId());
        if (seance == null) {
            throw new RestException(messageService.getMessage("seance.error.not-found", ticket.getSeance().getId()));
        }
        ticket.setSeance(seance);
        if (ticket.getPlace().getId() == null) {
            throw new RestException(messageService.getMessage("ticket.error.not-found.place"));
        }
        Place place = placeService.getById(ticket.getPlace().getId());
        if (place == null) {
            throw new RestException(messageService.getMessage("ticket.error.not-found.place"));
        }
        if (!checkCountPlaceInHall(ticket)) {
            throw new RestException(messageService.getMessage("ticket.error.invalid-count"));
        }

    }

    public Page<Ticket> getList(Map<String, Object> filter, Integer page, Integer perPage) {
        Page<Ticket> tickets;
        if (filter.get("id") != null) {
            Ticket t = new Ticket();
            t.setId((Integer) filter.get("id"));
            tickets = repository.findAll(Example.of(t), PageRequest.of(page, perPage));
        } else {
            tickets = repository.findAll(PageRequest.of(page, perPage, new Sort(Sort.Direction.ASC, "createdAt")));
        }
        return tickets;
    }

    public Ticket getById(Integer id) {
        Map<String, Object> filter = new HashMap<>();
        filter.putIfAbsent("id", id);
        List<Ticket> seances = getList(filter, 0, 1).getContent();
        return CollectionUtils.isEmpty(seances) ? null : seances.get(0);
    }

    public void buyTickets(List<Ticket> tickets, Order order) {
        repository.buyTickets(tickets, order);
    }

    public void cancelTickets(List<Ticket> cancelTickets, Order order) {
        repository.cancelTickets(cancelTickets, order);
    }

    public void cancelTicketsByOrder(Integer orderId) {
        repository.cancelTicketsByOrder(orderId);
    }

    public boolean checkBuyTickets(Integer seanceId) {
        return repository.checkBuyTickets(seanceId);
    }

    public boolean checkNotCancelOrder(Integer orderId) {

        Object[][] obj = repository.checkNotCancelOrder(orderId);
        LocalDateTime dateTime;
        if (obj.length!=0) {
             dateTime = LocalDateTime.of((LocalDate) obj[0][0], (LocalTime) obj[0][1]);
        }else {
            return false;
        }
        LocalDateTime time = LocalDateTime.now().plusHours(1);
        return time.isAfter(dateTime);
    }
}
