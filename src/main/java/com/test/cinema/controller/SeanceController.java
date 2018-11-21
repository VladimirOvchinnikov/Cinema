package com.test.cinema.controller;


import com.test.cinema.model.entity.Film;
import com.test.cinema.model.entity.Hall;
import com.test.cinema.model.entity.Seance;
import com.test.cinema.model.request.SeanceRequestBean;
import com.test.cinema.model.response.SeanceResponseBean;
import com.test.cinema.service.FilmService;
import com.test.cinema.service.HallService;
import com.test.cinema.service.SeanceService;
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
@RequestMapping("/seances")
public class SeanceController {

    @Autowired
    private SeanceService service;

    @Autowired
    private HallService hallService;
    @Autowired
    private FilmService filmService;

    @RequestMapping(method = RequestMethod.GET)
    public List<SeanceResponseBean> getList(@RequestParam(name = FILTER_NAME_PAGE, defaultValue = FILTER_NAME_PAGE_DEFAULT_VALUE) Integer page,
                                            @RequestParam(name = FILTER_NAME_PER_PAGE, defaultValue = FILTER_NAME_PER_PAGE_DEFAULT_VALUE) Integer perPage,
                                            HttpServletResponse response){
        if (page < DEFAULT_MIN_PAGE) {
            page = DEFAULT_MIN_PAGE;
        }

        if (perPage < 0) {
            perPage = DEFAULT_PAGE_SIZE;
        } else if (perPage > DEFAULT_MAX_PAGE_SIZE) {
            perPage = DEFAULT_MAX_PAGE_SIZE;
        }

        Map<String,Object> filter = new HashMap<>();
        Page<Seance> seances = service.getList(filter, page-1, perPage);

        response.setHeader(HEADER_NAME_PAGES, String.valueOf(seances.getTotalPages()));
        response.setHeader(HEADER_NAME_PAGE, String.valueOf(page));
        response.setHeader(HEADER_NAME_PAGE_SIZE, String.valueOf(perPage));

        return seances.stream().map(this::entityToBean).collect(Collectors.toList());
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public SeanceResponseBean getById(@PathVariable("id") Integer id){
        Seance seance = service.getById(id);
        if (seance == null){
            return null;
        }
        return entityToBean(seance);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public SeanceResponseBean create(@RequestBody SeanceRequestBean bean){
        Seance seance = beanToEntity(bean);
        seance = service.create(seance);
        return entityToBean(seance);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public SeanceResponseBean update(@PathVariable("id") Integer id,
                                     @RequestBody SeanceRequestBean bean){
        bean.setId(id);
        Seance seance = beanToEntity(bean);
        seance = service.update(seance);
        return entityToBean(seance);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Integer id){
        service.delete(id);
    }


    private SeanceResponseBean entityToBean(Seance entity){
        SeanceResponseBean bean = new SeanceResponseBean();
        bean.setId(entity.getId());
        bean.setCreatedAt(entity.getCreatedAt());
        bean.setDateSeance(entity.getDateSeance());
        bean.setFilmId(entity.getFilm().getId());
        bean.setHallId(entity.getHall().getId());
//        bean.setPrice(entity.getPrice());
        bean.setTimeSeance(entity.getTimeSeance());
        return bean;
    }

    private Seance beanToEntity(SeanceRequestBean bean) {
        Seance entity = new Seance();
        entity.setId(bean.getId());
        entity.setDateSeance(bean.getDateSeance());
        entity.setTimeSeance(bean.getTimeSeance());
//        entity.setPrice(bean.getPrice());
        Hall hall = new Hall();
        hall.setId(bean.getHallId());
        entity.setHall(hall);
        Film film = new Film();
        film.setId(bean.getFilmId());
        entity.setFilm(film);
//        entity.setHall(hallService.getById(bean.getHallId(), false));
//        entity.setFilm(filmService.getById(bean.getFilmId()));
        return entity;
    }
}
