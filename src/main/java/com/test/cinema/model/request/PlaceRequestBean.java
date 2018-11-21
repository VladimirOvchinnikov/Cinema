package com.test.cinema.model.request;

public class PlaceRequestBean implements RequestBean {

    private Integer id;
    private Integer placeNumber;
    private Boolean isVip;
    private Double priceCoefficient;

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

    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
    }

    public Double getPriceCoefficient() {
        return priceCoefficient;
    }

    public void setPriceCoefficient(Double priceCoefficient) {
        this.priceCoefficient = priceCoefficient;
    }
}
