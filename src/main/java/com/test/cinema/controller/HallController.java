package com.test.cinema.controller;

import com.test.cinema.model.entity.Hall;
import com.test.cinema.model.entity.Place;
import com.test.cinema.model.entity.Row;
import com.test.cinema.model.request.HallRequestBean;
import com.test.cinema.model.request.PlaceRequestBean;
import com.test.cinema.model.request.RowRequestBean;
import com.test.cinema.model.response.HallResponseBean;
import com.test.cinema.model.response.PlaceResponseBean;
import com.test.cinema.model.response.RowResponseBean;
import com.test.cinema.service.HallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.test.cinema.util.Constant.*;

//import static org.springframework.beans.support.PagedListHolder.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("/halls")
public class HallController {

    @Autowired
    private HallService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<HallResponseBean> getList(
//            @RequestParam(name = "id", required = false) Integer id,//Если получаем списком то не к чему нам пока инфа по всему содержимому залов
//            @RequestParam(name = "with_info", defaultValue = "false") Boolean withInfo,
            @RequestParam(name = FILTER_NAME_PAGE, defaultValue = FILTER_NAME_PAGE_DEFAULT_VALUE) Integer page,
            @RequestParam(name = FILTER_NAME_PER_PAGE, defaultValue = FILTER_NAME_PER_PAGE_DEFAULT_VALUE) Integer perPage,
            HttpServletResponse response
    ) {

        if (page < DEFAULT_MIN_PAGE) {
            page = DEFAULT_MIN_PAGE;
        }

        if (perPage < 0) {
            perPage = DEFAULT_PAGE_SIZE;
        } else if (perPage > DEFAULT_MAX_PAGE_SIZE) {
            perPage = DEFAULT_MAX_PAGE_SIZE;
        }

        Page<Hall> halls = service.getList(null, false, page-1, perPage);

        response.setHeader(HEADER_NAME_PAGES, String.valueOf(halls.getTotalPages()));
        response.setHeader(HEADER_NAME_PAGE, String.valueOf(page));
        response.setHeader(HEADER_NAME_PAGE_SIZE, String.valueOf(perPage));

        return halls.stream().map(h -> entityToBean(h, false)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public HallResponseBean getById(@PathVariable("id") Integer id) {
//                                    @RequestParam(name = "with_info", defaultValue = "false") Boolean withInfo) {//при запросе по id то информация по залу приходит полная
        Hall hall = service.getById(id, true);
        if (hall == null) {
            return null;
        }
        return entityToBean(hall, true);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public HallResponseBean create(@RequestBody HallRequestBean bean) {
        Hall hall = beanToEntity(bean);
        hall = service.create(hall);
        return entityToBean(hall, true);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public HallResponseBean update(@PathVariable("id") Integer id,
                                   @RequestBody HallRequestBean bean) {
        bean.setId(id);
        Hall hall = beanToEntity(bean);
        hall = service.update(hall);
        return entityToBean(hall, true);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }


    private Hall beanToEntity(HallRequestBean bean) {
        Hall entity = new Hall();
        entity.setId(bean.getId());
        entity.setName(bean.getName());
        List<Row> rows = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bean.getRows())) {
            for (RowRequestBean rowBean : bean.getRows()) {
                Row row = new Row();
                row.setId(rowBean.getId());
                row.setRowNumber(rowBean.getRowNumber());
                List<Place> places = new ArrayList<>();
                if (!CollectionUtils.isEmpty(rowBean.getPlaces())) {
                    for (PlaceRequestBean placeBean : rowBean.getPlaces()) {
                        Place place = new Place();
                        place.setId(placeBean.getId());
                        place.setPlaceNumber(placeBean.getPlaceNumber());
                        place.setIsVip(placeBean.getIsVip());
                        place.setPriceCoefficient(placeBean.getPriceCoefficient());
                        place.setRow(row);
                        places.add(place);
                    }
                }
                row.setPlaces(places);
                row.setHall(entity);
                rows.add(row);
            }
        }
        entity.setRows(rows);
        return entity;

    }

    private HallResponseBean entityToBean(Hall entity, Boolean withInfo) {
        HallResponseBean bean = new HallResponseBean();
        bean.setId(entity.getId());
        bean.setName(entity.getName());
        bean.setCreatedAt(entity.getCreatedAt());
        if (withInfo) {
            bean.setRowCount(entity.getRows().size());
            List<RowResponseBean> rowResponseBeans = new ArrayList<>();
            for (Row rowEntity : entity.getRows()) {
                RowResponseBean rowResponseBean = new RowResponseBean();
                rowResponseBean.setId(rowEntity.getId());
                rowResponseBean.setRowNumber(rowEntity.getRowNumber());
                rowResponseBean.setHallId(entity.getId());
                rowResponseBean.setPlaceCount(rowEntity.getPlaces().size());
                rowResponseBean.setCreatedAt(rowEntity.getCreatedAt());
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
                rowResponseBeans.add(rowResponseBean);
            }
            bean.setRows(rowResponseBeans);
        }
        return bean;
    }
}
