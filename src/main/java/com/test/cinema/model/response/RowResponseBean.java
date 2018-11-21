package com.test.cinema.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

import static com.test.cinema.util.Constant.PATTERN_DATE_TIME;

public class RowResponseBean implements ResponseBean {

    private Integer id;
    private Integer rowNumber;
    private Integer placeCount;
    private Integer hallId;
    private List<PlaceResponseBean> places;
    @JsonFormat(pattern = PATTERN_DATE_TIME)
    private LocalDateTime createdAt;

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

    public Integer getPlaceCount() {
        return placeCount;
    }

    public void setPlaceCount(Integer placeCount) {
        this.placeCount = placeCount;
    }

    public Integer getHallId() {
        return hallId;
    }

    public void setHallId(Integer hallId) {
        this.hallId = hallId;
    }

    public List<PlaceResponseBean> getPlaces() {
        return places;
    }

    public void setPlaces(List<PlaceResponseBean> places) {
        this.places = places;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
