package com.test.cinema.model.request;

import java.util.ArrayList;
import java.util.List;

public class RowRequestBean implements RequestBean {

    private Integer id;
    private Integer rowNumber;
    private List<PlaceRequestBean> places = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public List<PlaceRequestBean> getPlaces() {
        return places;
    }

    public void setPlaces(List<PlaceRequestBean> places) {
        this.places = places;
    }
}
