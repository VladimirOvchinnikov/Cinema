package com.test.cinema.service;

import com.test.cinema.exception.RestException;
import com.test.cinema.model.entity.Film;
import com.test.cinema.model.entity.Hall;
import com.test.cinema.model.entity.Seance;
import com.test.cinema.repository.SeanceRepository;
import com.test.cinema.service.util.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeanceService {

    @Autowired
    private SeanceRepository repository;

    @Autowired
    private FilmService filmService;

    @Autowired
    private HallService hallService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private MessageService messageService;

    public Page<Seance> getList(Map<String, Object> filter, Integer page, Integer perPage) {
        Page<Seance> seances;
        if (filter.get("id") != null) {
            Seance s = new Seance();
            s.setId((Integer) filter.get("id"));
            seances = repository.findAll(Example.of(s), PageRequest.of(page, perPage));
        } else {
            seances = repository.findAll(PageRequest.of(page, perPage, new Sort(Sort.Direction.ASC, "dateSeance", "timeSeance", "createdAt")));
        }
        return seances;
    }

    public Seance getById(Integer id) {
        Map<String, Object> filter = new HashMap<>();
        filter.putIfAbsent("id", id);
        List<Seance> seances = getList(filter, 0, 1).getContent();
        return CollectionUtils.isEmpty(seances) ? null : seances.get(0);
    }

    public Seance create(Seance seance) {
        validate(seance);
        seance = repository.saveAndFlush(seance);
        return seance;
    }

    public Seance update(Seance seance) {
        validate(seance);
        Seance instance = getById(seance.getId());
        if (instance == null){
            throw new RestException(messageService.getMessage("seance.error.not-found", seance.getId()));
        }
        seance.setCreatedAt(instance.getCreatedAt());
        seance = repository.saveAndFlush(seance);
        return seance;
    }

    @Transactional
    public void delete(Integer id) {
        checkBuyTickets(id);
        repository.delete(id);
    }

    private void checkBuyTickets(Integer id) {
        if (ticketService.checkBuyTickets(id)){
            throw new RestException(messageService.getMessage("seance.error.invalid.not-delete", id));
        }

    }

    private boolean checkDateAndTimeSeance(Seance seance) {

        List<Seance> s = repository.getSeanceByFilter(seance.getId(),
                seance.getHall(),
                seance.getDateSeance(),
                seance.getTimeSeance(),
                PageRequest.of(0, 1)
        );
        return !CollectionUtils.isEmpty(s);
    }

    private void validate(Seance seance) {
        if (seance.getFilm().getId() == null) {
            throw new RestException(messageService.getMessage("seance.error.not-found.film"));
        }
        Film film = filmService.getById(seance.getFilm().getId());
        if (film == null) {
            throw new RestException(messageService.getMessage("seance.error.not-found.film"));
        }
        seance.setFilm(film);

        if (seance.getHall().getId() == null) {
            throw new RestException(messageService.getMessage("seance.error.not-found.hall"));
        }
        Hall hall = hallService.getById(seance.getHall().getId(), false);
        if (hall == null) {
            throw new RestException(messageService.getMessage("seance.error.not-found.hall"));
        }
        seance.setHall(hall);

        if (seance.getDateSeance() == null) {
            throw new RestException(messageService.getMessage("seance.error.not-found.date-seance"));
        }

        if (seance.getTimeSeance() == null) {
            throw new RestException(messageService.getMessage("seance.error.not-found.time-seance"));
        }

        if (checkDateAndTimeSeance(seance)) {
            throw new RestException(messageService.getMessage("seance.error.seance-exist", seance.getHall().getName(),
                    seance.getHall().getId(), seance.getDateSeance(), seance.getTimeSeance()));
        }


    }

    public boolean checkSeanceWithFilm(Integer filmId) {
        return repository.checkSeanceWithFilm(filmId);
    }

    public boolean checkActiveSeance(Integer hallId) {
        return repository.checkActiveSeance(hallId);
    }
}
