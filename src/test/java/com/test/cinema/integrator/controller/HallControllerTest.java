package com.test.cinema.integrator.controller;

import com.test.cinema.exception.ExceptionResponse;
import com.test.cinema.integrator.util.Function;
import com.test.cinema.integrator.util.GenerateData;
import com.test.cinema.model.entity.Hall;
import com.test.cinema.model.entity.Place;
import com.test.cinema.model.entity.Row;
import com.test.cinema.model.entity.Seance;
import com.test.cinema.model.request.HallRequestBean;
import com.test.cinema.model.request.PlaceRequestBean;
import com.test.cinema.model.request.RowRequestBean;
import com.test.cinema.model.response.HallResponseBean;
import com.test.cinema.model.response.PlaceResponseBean;
import com.test.cinema.model.response.RowResponseBean;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.test.cinema.util.Constant.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class HallControllerTest extends BaseControllerTest<Hall, HallRequestBean, HallResponseBean> {


    private Hall postHall;
    private Hall putHall;

    @Override
    protected void prepareTestData() throws Exception {
        super.prepareTestData();
        path = "/halls";
        init();

        for (Hall hall : entities) {
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
    protected void dropTestData() throws Exception {
        super.dropTestData();
        GenerateData.dropData();
        getJdbcTemplate().execute("delete from places");
        getJdbcTemplate().execute("delete from rows");
        getJdbcTemplate().execute("delete from halls");
    }

    private void init() {
        postHall = new Hall();
        postHall.setName("Зал пост");
        List<Row> postRows = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Row row = new Row();
            row.setHall(postHall);
            row.setRowNumber(i + 1);
            List<Place> places = new ArrayList<>();
            for (int j = 0; j < i + 1; j++) {
                Place place = new Place();
                place.setPlaceNumber(j + 1);
                place.setIsVip(j % 2 == 1);
                place.setPriceCoefficient(place.getIsVip() ? 2.0 : 1.0);
                place.setRow(row);
                places.add(place);
            }
            row.setPlaces(places);
            postRows.add(row);
        }
        postHall.setRows(postRows);

        putHall = new Hall();
        putHall.setId(-1);
        putHall.setName("Зал пут");
        int pId = -1;
        int rId = -1;
        List<Row> putRows = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            Row row = new Row();
            if (rId != -2) {
                row.setId(rId--);
            } else {
                row.setId(null);
                rId--;
            }
            row.setHall(putHall);
            row.setRowNumber(i + 1);
            List<Place> places = new ArrayList<>();
            for (int j = 1; j < i + 1; j++) {
                Place place = new Place();
                if (row.getId() == null) {
                    place.setId(null);
                    pId--;
                } else if (row.getId() == -3) {
                    if (j == 1) {
                        place.setId(null);
                        pId--;
                    } else {
                        place.setId(pId--);
                    }
                } else {
                    place.setId(pId--);
                }
                place.setPlaceNumber(j + 1);
                place.setIsVip(j % 2 == 1);
                place.setPriceCoefficient(place.getIsVip() ? 2.0 : 1.0);
                place.setRow(row);
                places.add(place);
            }
            row.setPlaces(places);
            putRows.add(row);
        }
        putHall.setRows(putRows);


        createListHalls();
    }

    private void createListHalls() {
        Hall hall1 = new Hall();
        hall1.setId(-1);
        hall1.setName("Зал " + hall1.getId());
        List<Row> rows1 = new ArrayList<>();
        int pId = -1;
        int rId = -1;
        for (int i = 1; i < 4; i++) {
            Row row = new Row();
            row.setId(rId--);
            row.setHall(hall1);
            row.setRowNumber(i + 1);
            List<Place> places = new ArrayList<>();
            for (int j = 1; j < i + 1; j++) {
                Place place = new Place();
                place.setId(pId--);
                place.setPlaceNumber(j + 1);
                place.setIsVip(j % 2 == 1);
                place.setPriceCoefficient(place.getIsVip() ? 2.0 : 1.0);
                place.setRow(row);
                places.add(place);
            }
            row.setPlaces(places);
            rows1.add(row);
        }
        hall1.setRows(rows1);
        entities.add(hall1);

        for (int i = 2; i < countEntity + 1; i++) {
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
            entities.add(hall);
        }
    }

    @Test
    public void testPostHall() throws Exception {
        ResultActions ra = post(entityToRequestBean(postHall))
                .andExpect(status().isCreated());
        analiseResponse(ra, entityToResponseBean(postHall), null);
    }

    @Test
    public void testFailPostHall() throws Exception {
        validateTypicalException(entityToRequestBean(postHall), this::post);
    }

    @Test
    public void testGetHall() throws Exception {

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

    @Test
    public void testGetByIdHall() throws Exception {
        Hall hall = entities.get(0);
        ResultActions ra = get(hall.getId());
        analiseResponse(ra, entityToResponseBean(hall), null);

        ra = get(100);
        analiseResponse(ra, null, null);
    }


    @Test
    public void testGetEmptyListHalls() throws Exception {
        dropTestData();
        countEntity = 0;
        get();
    }

    @Test
    public void testDeleteHalls() throws Exception {
        Hall hall = entities.get(0);
        delete(hall.getId());
    }

    @Test
    public void testFailDeleteHall() throws Exception{
        Hall hall = GenerateData.generateSeances(1,1,1,false).get(0).getHall();
        Integer id = hall.getId();
        get(id).andExpect(jsonPath("$.id", is(id)));

        ExceptionResponse er = new ExceptionResponse();
        er.setCode(HttpStatus.BAD_REQUEST.value());
        er.setMessage(messageService.getMessage("hall.error.invalid.not-delete", id));
        MockHttpServletRequestBuilder delete = MockMvcRequestBuilders.delete(path + "/" + id);
        delete = delete.contentType(contentType);
        ResultActions ra = getMockMvc().perform(delete)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(er.getCode())))
                .andExpect(jsonPath("$.message", is(er.getMessage())));
    }

    @Test
    public void testPutHalls() throws Exception {
        ResultActions ra = get(putHall.getId())
                .andExpect(jsonPath("$", notNullValue(HallResponseBean.class)));
        HallResponseBean instanceBean = objectMapper.readValue(ra.andReturn().getResponse().getContentAsString(), HallResponseBean.class);

        Hall instance = responseBeanToEntity(instanceBean);
        ra = put(entityToRequestBean(instance));
        analiseResponse(ra, instanceBean, null);

        ra = put(entityToRequestBean(putHall));
        analiseResponse(ra, entityToResponseBean(putHall), null);
    }

    @Test
    public void testFailPutHall() throws Exception {

        HallRequestBean bean = entityToRequestBean(putHall);
        validateTypicalException(bean, this::put);

        ExceptionResponse er = new ExceptionResponse();
        er.setCode(HttpStatus.BAD_REQUEST.value());

        bean.setId(-11111);
        er.setMessage(messageService.getMessage("hall.error.not-found", bean.getName(), bean.getId()));
        exceptionResponse(put(bean), er);
        bean.setId(putHall.getId());

        bean.getRows().get(0).setId(-11111);
        er.setMessage(messageService.getMessage("hall.error.row.not-found", bean.getRows().get(0).getId()));
        exceptionResponse(put(bean), er);
        bean.getRows().get(0).setId(putHall.getRows().get(0).getId());

        bean.getRows().get(0).getPlaces().get(0).setId(-11111);
        er.setMessage(messageService.getMessage("hall.error.place.not-found", bean.getRows().get(0).getPlaces().get(0).getId()));
        exceptionResponse(put(bean), er);
        bean.getRows().get(0).getPlaces().get(0).setId(putHall.getRows().get(0).getPlaces().get(0).getId());

    }

    public void validateTypicalException(HallRequestBean bean, Function func) throws Exception {
        ExceptionResponse er = new ExceptionResponse();
        er.setCode(HttpStatus.BAD_REQUEST.value());

        er.setMessage(messageService.getMessage("hall.error.row.empty"));
        List<RowRequestBean> rows = bean.getRows();
        bean.setRows(null);
        exceptionResponse(func.exec(bean), er);
        bean.setRows(rows);

        er.setMessage(messageService.getMessage("hall.error.place.empty"));
        List<PlaceRequestBean> places = bean.getRows().get(0).getPlaces();
        bean.getRows().get(0).setPlaces(null);
        exceptionResponse(func.exec(bean), er);
        bean.getRows().get(0).setPlaces(places);
    }

    @Override
    public void analiseResponse(ResultActions ra, HallResponseBean bean, Integer index) throws Exception {
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
        ra = ra.andExpect(jsonPath(prePath + ".name", is(bean.getName())))
                .andExpect(jsonPath(prePath + ".row_count", is(bean.getRowCount())));
        if (bean.getCreatedAt() != null) {
            ra = ra.andExpect(jsonPath(prePath + ".created_at", is(getDateTime(bean.getCreatedAt()))));
        } else {
            ra = ra.andExpect(jsonPath(prePath + ".created_at", notNullValue(LocalDateTime.class)));
        }
        if (bean.getRows() != null) {
            ra.andExpect(jsonPath(prePath + ".rows", notNullValue(bean.getRows().getClass())));
            ra.andExpect(jsonPath(prePath + ".rows", hasSize(bean.getRows().size())));
            for (int i = 0; i < bean.getRows().size(); i++) {
                RowResponseBean row = bean.getRows().get(i);
                if (row.getId() != null) {
                    ra = ra.andExpect(jsonPath(prePath + ".rows[" + i + "].id", is(row.getId())))
                            .andExpect(jsonPath(prePath + ".rows[" + i + "].hall_id", is(row.getHallId())));
                } else {
                    ra = ra.andExpect(jsonPath(prePath + ".rows[" + i + "].id", notNullValue(Integer.class)))
                            .andExpect(jsonPath(prePath + ".rows[" + i + "].hall_id", notNullValue(Integer.class)));
                }
                ra = ra.andExpect(jsonPath(prePath + ".rows[" + i + "].row_number", is(row.getRowNumber())))
                        .andExpect(jsonPath(prePath + ".rows[" + i + "].place_count", is(row.getPlaceCount())))
                        .andExpect(jsonPath(prePath + ".rows[" + i + "].places", notNullValue(row.getPlaces().getClass())))
                        .andExpect(jsonPath(prePath + ".rows[" + i + "].places", hasSize(row.getPlaces().size())));
                if (row.getCreatedAt() != null) {
                    ra = ra.andExpect(jsonPath(prePath + ".rows[" + i + "].created_at", is(getDateTime(row.getCreatedAt()))));
                } else {
                    ra = ra.andExpect(jsonPath(prePath + ".rows[" + i + "].created_at", notNullValue(LocalDateTime.class)));
                }
                for (int j = 0; j < row.getPlaces().size(); j++) {
                    PlaceResponseBean place = row.getPlaces().get(j);
                    if (place.getId() != null) {
                        ra = ra.andExpect(jsonPath(prePath + ".rows[" + i + "].places[" + j + "].id", is(place.getId())))
                                .andExpect(jsonPath(prePath + ".rows[" + i + "].places[" + j + "].row_id", is(place.getRowId())));
                    } else {
                        ra = ra.andExpect(jsonPath(prePath + ".rows[" + i + "].places[" + j + "].id", notNullValue(Integer.class)))
                                .andExpect(jsonPath(prePath + ".rows[" + i + "].places[" + j + "].row_id", notNullValue(Integer.class)));
                    }
                    ra = ra.andExpect(jsonPath(prePath + ".rows[" + i + "].places[" + j + "].is_vip", is(place.getIsVip())))
                            .andExpect(jsonPath(prePath + ".rows[" + i + "].places[" + j + "].place_number", is(place.getPlaceNumber())))
                            .andExpect(jsonPath(prePath + ".rows[" + i + "].places[" + j + "].price_coefficient", is(place.getPriceCoefficient())));
                    if (place.getCreatedAt() != null) {

                        ra = ra.andExpect(jsonPath(prePath + ".rows[" + i + "].places[" + j + "].created_at", is(getDateTime(place.getCreatedAt()))));
                    } else {
                        ra = ra.andExpect(jsonPath(prePath + ".rows[" + i + "].places[" + j + "].created_at", notNullValue(LocalDateTime.class)));
                    }
                }
            }
        } else {
            ra.andExpect(jsonPath(prePath + ".rows", nullValue()));
        }
    }

    @Override
    public void prepareResponseBean(HallResponseBean bean) {
        bean.setRows(null);
        bean.setRowCount(null);
    }

    @Override
    public HallResponseBean entityToResponseBean(Hall entity) {
        HallResponseBean bean = new HallResponseBean();
        bean.setId(entity.getId());
        bean.setName(entity.getName());
        bean.setRowCount(entity.getRows().size());
        bean.setCreatedAt(entity.getCreatedAt());
        if (!CollectionUtils.isEmpty(entity.getRows())) {
            List<RowResponseBean> rowResponseBeans = new ArrayList<>();
            for (Row rowEntity : entity.getRows()) {
                RowResponseBean rowResponseBean = new RowResponseBean();
                rowResponseBean.setId(rowEntity.getId());
                rowResponseBean.setRowNumber(rowEntity.getRowNumber());
                rowResponseBean.setHallId(entity.getId());
                rowResponseBean.setCreatedAt(rowEntity.getCreatedAt());
                rowResponseBean.setPlaceCount(rowEntity.getPlaces().size());
                if (!CollectionUtils.isEmpty(rowEntity.getPlaces())) {
                    List<PlaceResponseBean> placeResponseBeans = new ArrayList<>();
                    for (Place placeEntity : rowEntity.getPlaces()) {
                        PlaceResponseBean placeResponseBean = new PlaceResponseBean();
                        placeResponseBean.setId(placeEntity.getId());
                        placeResponseBean.setPlaceNumber(placeEntity.getPlaceNumber());
                        placeResponseBean.setIsVip(placeEntity.getIsVip());
                        placeResponseBean.setPriceCoefficient(placeEntity.getPriceCoefficient());
                        placeResponseBean.setRowId(rowEntity.getId());
                        placeResponseBean.setCreatedAt(placeEntity.getCreatedAt());
                        placeResponseBeans.add(placeResponseBean);
                    }
                    rowResponseBean.setPlaces(placeResponseBeans);
                }
                rowResponseBeans.add(rowResponseBean);
            }
            bean.setRows(rowResponseBeans);
        }
        return bean;
    }

    @Override
    public HallRequestBean entityToRequestBean(Hall hall) {
        HallRequestBean bean = new HallRequestBean();
        bean.setId(hall.getId());
        bean.setName(hall.getName());
        if (!CollectionUtils.isEmpty(hall.getRows())) {
            List<RowRequestBean> rows = new ArrayList<>();
            for (Row row : hall.getRows()) {
                RowRequestBean rowBean = new RowRequestBean();
                rowBean.setId(row.getId());
                rowBean.setRowNumber(row.getRowNumber());
                if (!CollectionUtils.isEmpty(row.getPlaces())) {
                    List<PlaceRequestBean> places = new ArrayList<>();
                    for (Place place : row.getPlaces()) {
                        PlaceRequestBean placeBean = new PlaceRequestBean();
                        placeBean.setId(place.getId());
                        placeBean.setPlaceNumber(place.getPlaceNumber());
                        placeBean.setIsVip(place.getIsVip());
                        placeBean.setPriceCoefficient(place.getPriceCoefficient());
                        places.add(placeBean);
                    }
                    rowBean.setPlaces(places);
                }
                rows.add(rowBean);
            }
            bean.setRows(rows);
        }
        return bean;
    }

    @Override
    public Hall responseBeanToEntity(HallResponseBean bean) {
        Hall entity = new Hall();
        entity.setId(bean.getId());
        entity.setName(bean.getName());
        entity.setCreatedAt(bean.getCreatedAt());
        if (!CollectionUtils.isEmpty(bean.getRows())) {
            List<Row> rows = new ArrayList<>();
            for (RowResponseBean rowBean : bean.getRows()) {
                Row row = new Row();
                row.setId(rowBean.getId());
                row.setRowNumber(rowBean.getRowNumber());
                row.setHall(entity);
                row.setCreatedAt(rowBean.getCreatedAt());
                if (!CollectionUtils.isEmpty(rowBean.getPlaces())) {
                    List<Place> places = new ArrayList<>();
                    for (PlaceResponseBean placeBean : rowBean.getPlaces()) {
                        Place place = new Place();
                        place.setId(placeBean.getId());
                        place.setPlaceNumber(placeBean.getPlaceNumber());
                        place.setIsVip(placeBean.getIsVip());
                        place.setPriceCoefficient(placeBean.getPriceCoefficient());
                        place.setRow(row);
                        place.setCreatedAt(placeBean.getCreatedAt());
                        places.add(place);
                    }
                    row.setPlaces(places);
                }
                rows.add(row);
            }
            entity.setRows(rows);
        }
        return entity;
    }
}
