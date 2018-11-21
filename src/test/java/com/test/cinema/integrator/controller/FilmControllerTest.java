package com.test.cinema.integrator.controller;

import com.test.cinema.exception.ExceptionResponse;
import com.test.cinema.integrator.util.Function;
import com.test.cinema.integrator.util.GenerateData;
import com.test.cinema.model.entity.Film;
import com.test.cinema.model.request.FilmRequestBean;
import com.test.cinema.model.response.FilmResponseBean;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


import static com.test.cinema.util.Constant.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Ovchinnikov Vladimir email ovchinnikovvg@altarix.ru
 */
public class FilmControllerTest extends BaseControllerTest<Film, FilmRequestBean, FilmResponseBean> {

    private Film postFilm;
    private Film putFilm;

    private void init() {
        postFilm = new Film();
        postFilm.setName("Фильм для поста");
        postFilm.setPoster("что-то в base64");
        postFilm.setDuration(LocalTime.of(1, 45));
        postFilm.setPrice(250.00);
        postFilm.setStartOfHire(LocalDate.of(2018, 10, 6));
        postFilm.setEndOfHire(LocalDate.of(2018, 11, 6));

        for (int i = 1; i < countEntity + 1; i++) {
            Film film = new Film();
            film.setId(i * -1);
            film.setName("Фильм " + i);
            film.setPoster("Изображение " + i);
            film.setDuration(LocalTime.of(1, 30));
            film.setPrice(100.0);
            film.setStartOfHire(LocalDate.now().plusMonths(i));
            film.setEndOfHire(LocalDate.now().plusMonths(i + 1));
            entities.add(film);
        }

        putFilm = new Film();
        putFilm.setId(-1);
        putFilm.setName("Фильм для пут запроса");
        putFilm.setPoster("что-то в base64");
        putFilm.setDuration(LocalTime.of(1, 45));
        putFilm.setPrice(250.00);
        putFilm.setStartOfHire(LocalDate.of(2018, 10, 6));
        putFilm.setEndOfHire(LocalDate.of(2018, 11, 6));


    }

    @Override
    protected void prepareTestData() throws Exception {
        super.prepareTestData();
        path = "/films";
//        countEntity = 3;
        init();

        for (Film film : entities) {
            getJdbcTemplate().execute(String.format(
                    "insert into films (id, name, poster, duration, price, start_of_hire, end_of_hire) values (%s, '%s', '%s', '%s', %s, '%s', '%s')",
                    film.getId(), film.getName(), film.getPoster(), film.getDuration(), film.getPrice(), film.getStartOfHire(), film.getEndOfHire()));
        }
    }

    @Override
    protected void dropTestData() throws Exception {
        super.dropTestData();
        GenerateData.dropData();
        getJdbcTemplate().execute("delete from films");
    }

    @Test
    public void testPostFilm() throws Exception {
        ResultActions ra = post(entityToRequestBean(postFilm))
                .andExpect(status().isCreated());
        analiseResponse(ra, entityToResponseBean(postFilm), null);
    }

    @Test
    public void testFailPostFilm() throws Exception {
        validateTypicalExceptions(entityToRequestBean(postFilm), this::post);
    }

    @Test
    public void testPutFilm() throws Exception {
        FilmRequestBean bean = entityToRequestBean(putFilm);
        ResultActions ra = put(bean).andExpect(status().isOk());
        analiseResponse(ra, entityToResponseBean(putFilm), null);
    }

    @Test
    public void testFailPutFilm() throws Exception {
        FilmRequestBean bean = entityToRequestBean(putFilm);
        validateTypicalExceptions(bean, this::put);

        bean.setId(1000);
        ExceptionResponse er = new ExceptionResponse();
        er.setCode(HttpStatus.BAD_REQUEST.value());
        er.setMessage(messageService.getMessage("film.error.not-found.film", bean.getName(), bean.getId()));
        exceptionResponse(put(bean), er);
        bean.setId(putFilm.getId());

    }

    @Test
    public void testGetByIdFilm() throws Exception {
        Film film = entities.get(0);
        analiseResponse(get(film.getId()), entityToResponseBean(film), null);

        analiseResponse(get(11111), null, null);
    }

    @Test
    public void testGetFilm() throws Exception {
        get();

        putMapFilter(FILTER_NAME_PAGE, "2");
        get();

        putMapFilter(FILTER_NAME_PAGE, FILTER_NAME_PAGE_DEFAULT_VALUE);
        putMapFilter(FILTER_NAME_PER_PAGE, "1");
        get();

        putMapFilter(FILTER_NAME_PER_PAGE, "2");
        get();

        putMapFilter(FILTER_NAME_PAGE, "2");
        get();
    }

    @Test
    public void testGetEmptyFilms() throws Exception {
        dropTestData();
        countEntity=0;
        get();
    }

    @Test
    public void testDeleteFilm() throws Exception {
        Film film = entities.get(0);
        delete(film.getId());
    }

    @Test
    public void testFailDeleteFilm() throws  Exception{
        Film film = GenerateData.generateSeances(1,1,1,false).get(0).getFilm();
        Integer id = film.getId();
        get(id).andExpect(jsonPath("$.id", is(id)));

        ExceptionResponse er = new ExceptionResponse();
        er.setCode(HttpStatus.BAD_REQUEST.value());
        er.setMessage(messageService.getMessage("film.error.invalid.not-delete", id));
        MockHttpServletRequestBuilder delete = MockMvcRequestBuilders.delete(path + "/" + id);
        delete = delete.contentType(contentType);
        ResultActions ra = getMockMvc().perform(delete)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(er.getCode())))
                .andExpect(jsonPath("$.message", is(er.getMessage())));
    }

    @Override
    public void analiseResponse(ResultActions ra, FilmResponseBean bean, Integer index) throws Exception {
        if (bean == null) {
            ra.andExpect(content().string(isEmptyOrNullString()));
            return;
        }
        String prePath = "$" + (index != null ? "[" + index + "]" : "");
        if (bean.getId() == null) {
            ra = ra.andExpect(jsonPath(prePath + ".id", notNullValue()));
        } else {
            ra = ra.andExpect(jsonPath(prePath + ".id", is(bean.getId())));
        }
        ra.andExpect(jsonPath(prePath + ".name", is(bean.getName())))
                .andExpect(jsonPath(prePath + ".poster", is(bean.getPoster())))
                .andExpect(jsonPath(prePath + ".duration", is(getTime(bean.getDuration()))))
                .andExpect(jsonPath(prePath + ".price", is(bean.getPrice())))
                .andExpect(jsonPath(prePath + ".start_of_hire", is(getDate(bean.getStartOfHire()))))
                .andExpect(jsonPath(prePath + ".end_of_hire", is(getDate(bean.getEndOfHire()))));
        if (bean.getCreatedAt() != null){
            ra = ra.andExpect(jsonPath(prePath + ".created_at", is(getDateTime(bean.getCreatedAt()))));
        } else {
            ra = ra.andExpect(jsonPath(prePath + ".created_at", notNullValue(LocalDateTime.class)));
        }
    }

    private void validateTypicalExceptions(FilmRequestBean bean, Function func) throws Exception {
        ExceptionResponse er = new ExceptionResponse();
        er.setCode(HttpStatus.BAD_REQUEST.value());
        bean.setName("");
        er.setMessage(messageService.getMessage("film.error.not_found.name"));
        exceptionResponse(func.exec(bean), er);
        bean.setName(postFilm.getName());

        bean.setPrice(null);
        er.setMessage(messageService.getMessage("film.error.not_found.price"));
        exceptionResponse(func.exec(bean), er);
        bean.setPrice(postFilm.getPrice());

        bean.setStartOfHire(null);
        er.setMessage(messageService.getMessage("film.error.not_found.start-of-hire"));
        exceptionResponse(func.exec(bean), er);
        bean.setStartOfHire(postFilm.getStartOfHire());

        bean.setEndOfHire(null);
        er.setMessage(messageService.getMessage("film.error.not_found.end-of-hire"));
        exceptionResponse(func.exec(bean), er);
        bean.setEndOfHire(postFilm.getEndOfHire());


        bean.setEndOfHire(bean.getStartOfHire().minusDays(1));
        er.setMessage(messageService.getMessage("film.error.invalid.start-end-of-fire"));
        exceptionResponse(func.exec(bean), er);
        bean.setEndOfHire(postFilm.getEndOfHire());
    }

    @Override
    public FilmRequestBean entityToRequestBean(Film entity) {
        FilmRequestBean bean = new FilmRequestBean();
        bean.setId(entity.getId());
        bean.setName(entity.getName());
        bean.setPoster(entity.getPoster());
        bean.setDuration(entity.getDuration());
        bean.setPrice(entity.getPrice());
        bean.setStartOfHire(entity.getStartOfHire());
        bean.setEndOfHire(entity.getEndOfHire());
        return bean;
    }

    @Override
    public FilmResponseBean entityToResponseBean(Film entity) {
        FilmResponseBean bean = new FilmResponseBean();
        bean.setId(entity.getId());
        bean.setName(entity.getName());
        bean.setPoster(entity.getPoster());
        bean.setDuration(entity.getDuration());
        bean.setPrice(entity.getPrice());
        bean.setStartOfHire(entity.getStartOfHire());
        bean.setEndOfHire(entity.getEndOfHire());
        bean.setCreatedAt(entity.getCreatedAt());
        return bean;
    }

    @Override
    public Film responseBeanToEntity(FilmResponseBean bean){
        return null;
    }
}
