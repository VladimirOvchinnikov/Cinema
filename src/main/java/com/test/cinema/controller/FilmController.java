package com.test.cinema.controller;

import com.test.cinema.model.entity.Film;
import com.test.cinema.model.request.FilmRequestBean;
import com.test.cinema.model.response.FilmResponseBean;
import com.test.cinema.service.FilmService;
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

import static com.test.cinema.util.Constant.*;

@RestController
@RequestMapping("/films")
public class FilmController {

    @Autowired
    private FilmService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<FilmResponseBean> getList(@RequestParam(name = FILTER_NAME_PAGE, defaultValue = FILTER_NAME_PAGE_DEFAULT_VALUE) Integer page,
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
        Page<Film> films = service.getList(filter, page-1, perPage);

        response.setHeader(HEADER_NAME_PAGES, String.valueOf(films.getTotalPages()));
        response.setHeader(HEADER_NAME_PAGE, String.valueOf(page));
        response.setHeader(HEADER_NAME_PAGE_SIZE, String.valueOf(perPage));
        List<FilmResponseBean> beans = new ArrayList<>();
        films.forEach(f -> beans.add(entityToBean(f)));
        return beans;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public FilmResponseBean getById(@PathVariable("id") Integer id) {
        Film film = service.getById(id);
        if (film == null) {
            return null;
        }
        return entityToBean(film);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public FilmResponseBean create(@RequestBody FilmRequestBean bean) {

        Film create = beanToEntity(bean);
        create = service.create(create);
        return entityToBean(create);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public FilmResponseBean update(@PathVariable("id") Integer id,
                                   @RequestBody FilmRequestBean bean) {
        bean.setId(id);
        Film update = beanToEntity(bean);
        update = service.update(update);
        return entityToBean(update);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }

    private Film beanToEntity(FilmRequestBean bean) {
        Film film = new Film();
        film.setId(bean.getId());
        film.setName(bean.getName());
        film.setPoster(bean.getPoster());
        film.setDuration(bean.getDuration());
        film.setPrice(bean.getPrice());
        film.setStartOfHire(bean.getStartOfHire());
        film.setEndOfHire(bean.getEndOfHire());
        return film;
    }

    private FilmResponseBean entityToBean(Film film) {
        FilmResponseBean bean = new FilmResponseBean();
        bean.setId(film.getId());
        bean.setName(film.getName());
        bean.setPoster(film.getPoster());
        bean.setDuration(film.getDuration());
        bean.setPrice(film.getPrice());
        bean.setStartOfHire(film.getStartOfHire());
        bean.setEndOfHire(film.getEndOfHire());
        bean.setCreatedAt(film.getCreatedAt());
        return bean;
    }
}
