package com.test.cinema.service;

import com.test.cinema.exception.RestException;
import com.test.cinema.model.entity.Hall;
import com.test.cinema.model.entity.Row;
import com.test.cinema.repository.HallRepository;
import com.test.cinema.service.util.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HallService {

    @Autowired
    private HallRepository repository;

    @Autowired
    private SeanceService seanceService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RowService rowService;

    public Page<Hall> getList(Integer id, Boolean withInfo, Integer page, Integer perPage) {
        Page<Hall> pp;
        if (id != null) {
            Hall h = new Hall();
            h.setId(id);
            pp = repository.findAll(Example.of(h),
                    PageRequest.of(0, 1, new Sort(Sort.Direction.ASC, "createdAt")));
        } else {
            pp = repository.findAll(PageRequest.of(page, perPage, new Sort(Sort.Direction.ASC, "createdAt")));
        }

        if (withInfo) {
            pp.getContent().forEach(h -> h.setRows(loadRows(h)));
        }
        return pp;
    }

    public Hall getById(Integer id, Boolean withInfo) {
        List<Hall> halls = getList(id, withInfo, 0, 1).getContent();
        return CollectionUtils.isEmpty(halls) ? null : halls.get(0);
    }

    @Transactional
    public Hall create(Hall hall) {
        validateHall(hall);
        hall = repository.save(hall);
        hall.getRows().forEach(r->rowService.create(r));
        repository.flush();
        return getById(hall.getId(), true);
    }

    @Transactional
    public Hall update(Hall hall) {
        validateHall(hall);
        Hall instance = getById(hall.getId(), true);
        if (instance == null){
            throw new RestException(messageService.getMessage("hall.error.not-found", hall.getName(), hall.getId()));
        }
        hall.setCreatedAt(instance.getCreatedAt());

        List<Row> insertRows = hall.getRows().stream().filter(r -> r.getId() == null).collect(Collectors.toList());
        List<Row> updateRows = hall.getRows().stream().filter(r -> r.getId() != null).collect(Collectors.toList());
        List<Row> deleteRows = instance.getRows().stream().filter(r -> !updateRows.contains(r)).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(insertRows)) {
            rowService.createRows(insertRows);
        }
        if (!CollectionUtils.isEmpty(deleteRows)) {
            rowService.deleteRows(deleteRows);
        }
        if (!CollectionUtils.isEmpty(updateRows)) {
            rowService.updateRows(updateRows);
        }

        hall = repository.save(hall);
        repository.flush();
        return getById(hall.getId(), true);
    }

    @Transactional
    public void delete(Integer id) {
        checkActiveSeance(id);
        rowService.deleteByHallId(id);
        repository.delete(id);
    }

    public Integer loadRowCount(Hall entity) {
        return rowService.getRowCount(entity);
    }

    public List<Row> loadRows(Hall entity) {
        return rowService.getRowsByHall(entity);
    }

    private void checkActiveSeance(Integer hallId){
        if (seanceService.checkActiveSeance(hallId)){
            throw new RestException(messageService.getMessage("hall.error.invalid.not-delete", hallId));
        }
    }

    private void validateHall(Hall hall){
        if (CollectionUtils.isEmpty(hall.getRows())){
            throw new RestException(messageService.getMessage("hall.error.row.empty"));//hall.error.row.not-found
        }
    }
}
