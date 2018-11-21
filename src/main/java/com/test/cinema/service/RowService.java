package com.test.cinema.service;

import com.test.cinema.exception.RestException;
import com.test.cinema.model.entity.Hall;
import com.test.cinema.model.entity.Place;
import com.test.cinema.model.entity.Row;
import com.test.cinema.repository.RowRepository;
import com.test.cinema.service.util.MessageService;
import jdk.nashorn.internal.runtime.options.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RowService {

    @Autowired
    private RowRepository repository;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private MessageService messageService;

    @Transactional
    public void create(Row row) {
        validateRow(row);
        repository.save(row);
        row.getPlaces().forEach(p -> placeService.create(p));
    }

    @Transactional
    public void update(Row row) {
//        row.getPlaces().forEach(p -> placeService.update(p));
        repository.save(row);
    }

    public Integer getRowCount(Hall entity) {
        return repository.getRowCount(entity);
    }

    public List<Row> getRowsByHall(Hall entity) {
        List<Row> rows = repository.getRowsByHall(entity);
        rows.forEach(r -> r.setPlaces(placeService.getPlacesByRow(r)));
        return rows;
    }

    @Transactional
    public void deleteByHallId(Integer hallId) {
        placeService.deleteByHallId(hallId);
        repository.deleteByHallId(hallId);
    }

    private void validateRow(Row row) {
        if (CollectionUtils.isEmpty(row.getPlaces())) {
            throw new RestException(messageService.getMessage("hall.error.place.empty"));
        }
    }

    @Transactional
    public void createRows(List<Row> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return;
        }
        rows.forEach(this::validateRow);
        repository.saveAll(rows);
        List<Place> places = new ArrayList<>();
        rows.forEach(r -> places.addAll(r.getPlaces()));
        placeService.createPlaces(places);
    }

    @Transactional
    public void deleteRows(List<Row> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return;
        }
        List<Place> places = new ArrayList<>();
        rows.forEach(r -> places.addAll(r.getPlaces()));
        if (!CollectionUtils.isEmpty(places)) {
            placeService.deletePlaces(places);
        }
        repository.deleteRows(rows);
    }

    public void updateRows(List<Row> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return;
        }
        rows.forEach(this::validateRow);

        List<Place> insertPlaces = new ArrayList<>();
        List<Place> updatePlaces = new ArrayList<>();
        List<Place> deletePlaces = new ArrayList<>();
        for (Row row : rows) {
            Row instance = getById(row.getId());
            if (instance == null) {
                throw new RestException(messageService.getMessage("hall.error.row.not-found", row.getId()));
            }
            row.setCreatedAt(instance.getCreatedAt());
            insertPlaces.addAll(row.getPlaces().stream().filter(p -> p.getId() == null).collect(Collectors.toList()));
            updatePlaces.addAll(row.getPlaces().stream().filter(p -> p.getId() != null).collect(Collectors.toList()));
            deletePlaces.addAll(instance.getPlaces().stream().filter(r -> !updatePlaces.contains(r)).collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(insertPlaces)) {
            placeService.createPlaces(insertPlaces);
        }
        if (!CollectionUtils.isEmpty(deletePlaces)) {
            placeService.deletePlaces(deletePlaces);
        }
        if (!CollectionUtils.isEmpty(updatePlaces)) {
            placeService.updatePlaces(updatePlaces);
        }
        repository.saveAll(rows);
    }

    private Row getById(Integer id) {
        Optional<Row> optional = repository.findById(id);
        return optional.orElse(null);
    }
}
