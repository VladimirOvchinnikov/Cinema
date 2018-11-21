package com.test.cinema.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import static com.test.cinema.util.Constant.PATTERN_DATE_TIME;

public class PlaceResponseBean implements ResponseBean {

    private Integer id;
    private Integer placeNumber;
    private Boolean isVip;
    private Double priceCoefficient;
    private Integer rowId;
    @JsonFormat(pattern = PATTERN_DATE_TIME)
    private LocalDateTime createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlaceNumber() {
        return placeNumber;
    }

    public void setPlaceNumber(Integer placeNumber) {
        this.placeNumber = placeNumber;
    }

    public Boolean getIsVip() {
        return isVip;
    }

    public void setIsVip(Boolean VIP) {
        isVip = VIP;
    }

    public Double getPriceCoefficient() {
        return priceCoefficient;
    }

    public void setPriceCoefficient(Double priceCoefficient) {
        this.priceCoefficient = priceCoefficient;
    }

    public Integer getRowId() {
        return rowId;
    }

    public void setRowId(Integer rowId) {
        this.rowId = rowId;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
