package com.test.cinema.service;

import com.test.cinema.exception.RestException;
import com.test.cinema.model.entity.Film;
import com.test.cinema.repository.FilmRepository;
import com.test.cinema.service.util.MessageService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FilmService {

    @Autowired
    private FilmRepository repository;

    @Autowired
    private SeanceService seanceService;

    @Autowired
    private MessageService messageService;

    public Page<Film> getList(Map<String, Object> filter, Integer page, Integer perPage) {
        Page<Film> films;
        if (filter.get("id") != null) {
            Film f = new Film();
            f.setId((Integer) filter.get("id"));
            films = repository.findAll(Example.of(f), PageRequest.of(page, perPage));
        } else {
            films = repository.selectFilms(PageRequest.of(page, perPage, new Sort(Sort.Direction.ASC, "startOfHire", "name", "createdAt")));
        }
        return films;
    }

    public Film getById(Integer id) {
        Map<String, Object> filter = new HashMap<>();
        filter.putIfAbsent("id", id);
        List<Film> films = getList(filter, 0, 1).getContent();
        return CollectionUtils.isEmpty(films) ? null : films.get(0);
    }

    public Film create(Film create) {
        validate(create);
        repository.saveAndFlush(create);
//        repository.flush();
        return getById(create.getId());
    }

    public Film update(Film update) {
        validate(update);
        Film instance = getById(update.getId());
        if (instance == null) {
            throw new RestException(messageService.getMessage("film.error.not-found.film", update.getName(), update.getId()));
        }
        update.setCreatedAt(instance.getCreatedAt());
        update = repository.saveAndFlush(update);
//        repository.flush()

        return getById(update.getId());
    }

    @Transactional
    public void delete(Integer id) {
        checkDeleteFilm(id);
        repository.delete(id);
    }

    private void checkDeleteFilm(Integer id) {
        if (seanceService.checkSeanceWithFilm(id)){
            throw new RestException(messageService.getMessage("film.error.invalid.not-delete", id));
        }
    }

    private void validate(Film film) {
        if (StringUtils.isEmpty(film.getName())) {
            throw new RestException(messageService.getMessage("film.error.not_found.name"));
        }
        if (film.getPrice() == null) {
            throw new RestException(messageService.getMessage("film.error.not_found.price"));
        }
        if (film.getStartOfHire() == null) {
            throw new RestException(messageService.getMessage("film.error.not_found.start-of-hire"));
        }
        if (film.getEndOfHire() == null) {
            throw new RestException(messageService.getMessage("film.error.not_found.end-of-hire"));
        }
        if (film.getStartOfHire().isAfter(film.getEndOfHire())) {
            throw new RestException(messageService.getMessage("film.error.invalid.start-end-of-fire"));
        }
    }
}
