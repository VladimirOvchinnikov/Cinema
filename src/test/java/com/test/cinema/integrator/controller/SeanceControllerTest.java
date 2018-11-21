package com.test.cinema.integrator.controller;

import com.test.cinema.exception.ExceptionResponse;
import com.test.cinema.integrator.util.Function;
import com.test.cinema.integrator.util.GenerateData;
import com.test.cinema.model.entity.*;
import com.test.cinema.model.request.HallRequestBean;
import com.test.cinema.model.request.SeanceRequestBean;
import com.test.cinema.model.response.SeanceResponseBean;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.test.cinema.util.Constant.FILTER_NAME_PAGE;
import static com.test.cinema.util.Constant.FILTER_NAME_PER_PAGE;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SeanceControllerTest extends BaseControllerTest<Seance, SeanceRequestBean, SeanceResponseBean> {

    private Seance postSeance;
    private Seance putSeance;

    private Map<Integer, Hall> halls;
    private Map<Integer, Film> films;
//    private List<Hall> halls;
//    private List<Film> films;

    private void init() {
        createHalls();
        createFilms();

        postSeance = new Seance();
        postSeance.setFilm(films.get(-1));
        postSeance.setHall(halls.get(-2));
        postSeance.setDateSeance(LocalDate.now().plusDays(2));
        postSeance.setTimeSeance(LocalTime.of(1, 30));

        putSeance = new Seance();
        putSeance.setId(-1);
        putSeance.setFilm(films.get(-2));
        putSeance.setHall(halls.get(-2));
        putSeance.setDateSeance(LocalDate.now().plusDays(1));
        putSeance.setTimeSeance(LocalTime.of(2, 15));

        for (int i = 1; i < countEntity + 1; i++) {
            Seance seance = new Seance();
            seance.setId(-1 * i);
            seance.setFilm(films.get(-1 * i));
            seance.setHall(halls.get(-1 * i));
            seance.setDateSeance(LocalDate.now());
            seance.setTimeSeance(LocalTime.of(1, 30));
            entities.add(seance);
        }
    }

    private void createFilms() {
        films = new HashMap<>();
        for (int i = 1; i < countEntity + 1; i++) {
            Film film = new Film();
            film.setId(i * -1);
            film.setName("Фильм " + film.getId());
            film.setPoster("Изображение " + film.getId());
            film.setDuration(LocalTime.of(1, 30));
            film.setPrice(100.0);
            film.setStartOfHire(LocalDate.now().plusMonths(i));
            film.setEndOfHire(LocalDate.now().plusMonths(i + 1));
            films.putIfAbsent(film.getId(), film);
        }

        for (Film film : films.values()) {
            getJdbcTemplate().execute(String.format(
                    "insert into films (id, name, poster, duration, price, start_of_hire, end_of_hire) values (%s, '%s', '%s', '%s', %s, '%s', '%s')",
                    film.getId(), film.getName(), film.getPoster(), film.getDuration(), film.getPrice(), film.getStartOfHire(), film.getEndOfHire()));
        }
    }

    private void createHalls() {
        halls = new HashMap<>();
        int pId = -1;
        int rId = -1;
        for (int i = 1; i < countEntity + 1; i++) {
            Hall hall = new Hall();
            hall.setId(-1 * (i));
            hall.setName("Тест Зал " + hall.getId());
            List<Row> rows = new ArrayList<>();
            for (int i1 = 1; i1 < (i + 1); i1++) {
                Row row = new Row();
                row.setId(rId--);
                row.setHall(hall);
                row.setRowNumber(i1);
                List<Place> places = new ArrayList<>();
                for (int j = 1; j < i1 + 1; j++) {
                    Place place = new Place();
                    place.setId(pId--);
                    place.setPlaceNumber(j);
                    place.setIsVip(j % 2 == 1);
                    place.setPriceCoefficient(place.getIsVip() ? 2.0 : 1.0);
                    place.setRow(row);
                    places.add(place);
                }
                row.setPlaces(places);
                rows.add(row);
            }
            hall.setRows(rows);
            halls.putIfAbsent(hall.getId(), hall);
        }

        for (Hall hall : halls.values()) {
            getJdbcTemplate().execute(String.format("insert into halls (id, name) values (%s, '%s')", hall.getId(), hall.getName()));

            for (Row row : hall.getRows()) {
                getJdbcTemplate().execute(String.format("insert into rows (id, row_number, hall_id) values (%s, %s, %s)", row.getId(), row.getRowNumber(), hall.getId()));
                for (Place place : row.getPlaces()) {
                    getJdbcTemplate().execute(String.format(
                            "insert into places (id, place_number, is_vip, price_coefficient, row_id) values (%s, %s, %s, %s, %s)",
                            place.getId(), place.getPlaceNumber(), place.getIsVip(), place.getPriceCoefficient(), row.getId()));
                }
            }
        }
    }

    @Override
    protected void prepareTestData() throws Exception {
        super.prepareTestData();
        path = "/seances";
        init();

        for (Seance seance : entities) {
            getJdbcTemplate().execute(String.format(
                    "insert into seances (id, film_id, hall_id, date_seance, time_seance) values (%s, '%s', '%s', '%s', '%s')",
                    seance.getId(), seance.getFilm().getId(), seance.getHall().getId(), seance.getDateSeance(), seance.getTimeSeance()));
        }
    }

    @Override
    protected void dropTestData() throws Exception {
        super.dropTestData();
        GenerateData.dropData();
        getJdbcTemplate().execute("delete from seances");
        getJdbcTemplate().execute("delete from films");
        getJdbcTemplate().execute("delete from places");
        getJdbcTemplate().execute("delete from rows");
        getJdbcTemplate().execute("delete from halls");
    }

    @Test
    public void testPostSeance() throws Exception {
        ResultActions ra = post(entityToRequestBean(postSeance))
                .andExpect(status().isCreated());
        analiseResponse(ra, entityToResponseBean(postSeance), null);
    }

    @Test
    public void testFailPostSeance() throws Exception {
        validateTypicalException(entityToRequestBean(postSeance), this::post);
    }

    private void validateTypicalException(SeanceRequestBean bean, Function func) throws Exception {
        ExceptionResponse er = new ExceptionResponse();
        er.setCode(HttpStatus.BAD_REQUEST.value());

        bean.setFilmId(null);
        er.setMessage(messageService.getMessage("seance.error.not-found.film"));
        exceptionResponse(func.exec(bean), er);
        bean.setFilmId(-11111);
        exceptionResponse(func.exec(bean), er);
        bean.setFilmId(postSeance.getFilm().getId());

        bean.setHallId(null);
        er.setMessage(messageService.getMessage("seance.error.not-found.hall"));
        exceptionResponse(func.exec(bean), er);
        bean.setHallId(-11111);
        exceptionResponse(func.exec(bean), er);
        bean.setHallId(postSeance.getHall().getId());

        bean.setDateSeance(null);
        er.setMessage(messageService.getMessage("seance.error.not-found.date-seance"));
        exceptionResponse(func.exec(bean), er);
        bean.setDateSeance(postSeance.getDateSeance());

        bean.setTimeSeance(null);
        er.setMessage(messageService.getMessage("seance.error.not-found.time-seance"));
        exceptionResponse(func.exec(bean), er);
        bean.setTimeSeance(postSeance.getTimeSeance());

        bean.setDateSeance(LocalDate.now());
        er.setMessage(messageService.getMessage("seance.error.seance-exist", postSeance.getHall().getName(),
                bean.getHallId(), bean.getDateSeance(), bean.getTimeSeance()));
        exceptionResponse(func.exec(bean), er);
        bean.setTimeSeance(bean.getTimeSeance().plusMinutes(1));
        er.setMessage(messageService.getMessage("seance.error.seance-exist", postSeance.getHall().getName(),
                bean.getHallId(), bean.getDateSeance(), bean.getTimeSeance()));
        exceptionResponse(func.exec(bean), er);
        bean.setTimeSeance(bean.getTimeSeance().plusMinutes(89));
        er.setMessage(messageService.getMessage("seance.error.seance-exist", postSeance.getHall().getName(),
                bean.getHallId(), bean.getDateSeance(), bean.getTimeSeance()));
        exceptionResponse(func.exec(bean), er);
        bean.setTimeSeance(postSeance.getTimeSeance());
        bean.setDateSeance(postSeance.getDateSeance());
    }

    @Test
    public void testPutSeance() throws Exception {
        Seance seance = entities.get(0);
        ResultActions ra = put(entityToRequestBean(seance))
                .andExpect(status().isOk());
        analiseResponse(ra, entityToResponseBean(seance), null);

        ra = put(entityToRequestBean(putSeance))
                .andExpect(status().isOk());
        analiseResponse(ra, entityToResponseBean(putSeance), null);

    }

    @Test
    public void testFailPutSeance() throws Exception {
        SeanceRequestBean bean = entityToRequestBean(putSeance);
        validateTypicalException(bean, this::put);

        bean.setId(-11111);
        ExceptionResponse er = new ExceptionResponse();
        er.setCode(HttpStatus.BAD_REQUEST.value());
        er.setMessage(messageService.getMessage("seance.error.not-found", bean.getId()));
        exceptionResponse(put(bean), er);
        bean.setId(putSeance.getId());
    }

    @Test
    public void testDeleteSeance() throws Exception {
        delete(entities.get(0).getId());
    }

    @Test
    public void testDeleteFailSeance() throws Exception{
        Order order = new Order();
        order.setUser(GenerateData.generateUsers(1,false).get(0));
        order.setId(GenerateData.getLastId("orders"));
        order.setBonusCount(0);
        order.setTicketCount(1);
        order.setTotalPrice(1D);
        order.setTickets(GenerateData.generateTickets(1,1,1,false));

        Order order1 = GenerateData.generateOrders(Collections.singletonList(order),false).get(0);

        Integer id = order1.getTickets().get(0).getSeance().getId();
        get(id).andExpect(jsonPath("$.id", is(id)));

        ExceptionResponse er = new ExceptionResponse();
        er.setCode(HttpStatus.BAD_REQUEST.value());
        er.setMessage(messageService.getMessage("seance.error.invalid.not-delete", id));
        MockHttpServletRequestBuilder delete = MockMvcRequestBuilders.delete(path + "/" + id);
        delete = delete.contentType(contentType);
        ResultActions ra = getMockMvc().perform(delete)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(er.getCode())))
                .andExpect(jsonPath("$.message", is(er.getMessage())));
    }

    @Test
    public void testGetByIdSeance() throws Exception {
        Seance seance = entities.get(0);
        ResultActions ra = get(seance.getId());
        analiseResponse(ra, entityToResponseBean(seance), null);

        ra = get(-11111);
        analiseResponse(ra, null, null);
    }

    @Test
    public void testGetSeance() throws Exception {
        ResultActions ra = get();

        putMapFilter(FILTER_NAME_PER_PAGE, "1");
        ra = get();

        putMapFilter(FILTER_NAME_PAGE, "2");
        ra = get();

        putMapFilter(FILTER_NAME_PAGE, "3");
        ra = get();

        putMapFilter(FILTER_NAME_PAGE, "4");
        ra = get();
    }

    @Override
    public void analiseResponse(ResultActions ra, SeanceResponseBean bean, Integer index) throws Exception {
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
        if (bean.getCreatedAt() != null) {
            ra = ra.andExpect(jsonPath(prePath + ".created_at", is(getDateTime(bean.getCreatedAt()))));
        } else {
            ra = ra.andExpect(jsonPath(prePath + ".created_at", notNullValue(LocalDateTime.class)));
        }
        ra = ra.andExpect(jsonPath(prePath + ".film_id", is(bean.getFilmId())))
                .andExpect(jsonPath(prePath + ".hall_id", is(bean.getHallId())))
                .andExpect(jsonPath(prePath + ".date_seance", is(getDate(bean.getDateSeance()))))
                .andExpect(jsonPath(prePath + ".time_seance", is(getTime(bean.getTimeSeance()))));
    }

    @Override
    public SeanceRequestBean entityToRequestBean(Seance entity) {
        SeanceRequestBean bean = new SeanceRequestBean();
        bean.setId(entity.getId());
        bean.setDateSeance(entity.getDateSeance());
        bean.setFilmId(entity.getFilm().getId());
        bean.setHallId(entity.getHall().getId());
//        bean.setPrice(entity.getPrice());
        bean.setTimeSeance(entity.getTimeSeance());
        return bean;
    }

    @Override
    public SeanceResponseBean entityToResponseBean(Seance entity) {
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

    @Override
    public Seance responseBeanToEntity(SeanceResponseBean bean) {
        return null;
    }
}
