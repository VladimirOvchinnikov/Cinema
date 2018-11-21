package com.test.cinema.service;

import com.test.cinema.exception.RestException;
import com.test.cinema.model.entity.Place;
import com.test.cinema.model.entity.Row;
import com.test.cinema.repository.PlaceRepository;
import com.test.cinema.service.util.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Service
public class PlaceService {

    @Autowired
    private PlaceRepository repository;

    @Autowired
    private MessageService messageService;

    public void create(Place place) {
        repository.save(place);
    }

    public void update(Place place) {
        repository.save(place);
    }

    public List<Place> getPlacesByRow(Row row) {
        return repository.getPlacesByRow(row);
    }

    public void deleteByHallId(Integer hallId) {
        repository.deleteByHallId(hallId);
    }

    @Transactional
    public void createPlaces(List<Place> places) {
        if (CollectionUtils.isEmpty(places)) {
            return;
        }
        repository.saveAll(places);
    }

    @Transactional
    public void deletePlaces(List<Place> places) {
        if (CollectionUtils.isEmpty(places)) {
            return;
        }
        checkTicketIsSoldInPlaces(places);
        repository.deletePlaces(places);
    }

    public void checkTicketIsSoldInPlace(Place place) {
        //TODO написать код для проверки что на данное место продан билет на будущие сеансы
    }

    public void checkTicketIsSoldInPlaces(List<Place> places) {
        //TODO написать код для проверки что хотя бы на 1 место продан билет на будущие сеансы
    }

    public void updatePlaces(List<Place> places) {
        if (CollectionUtils.isEmpty(places)) {
            return;
        }
        for (Place place : places) {
            Place instance = getById(place.getId());
            if (instance == null) {
                throw new RestException(messageService.getMessage("hall.error.place.not-found", place.getId()));
            }
            place.setCreatedAt(instance.getCreatedAt());
        }
        repository.saveAll(places);
    }

    public Place getById(Integer id) {
        Optional<Place> optional = repository.findById(id);
        return optional.orElse(null);
    }
}
