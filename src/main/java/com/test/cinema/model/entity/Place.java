package com.test.cinema.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "v_places")
public class Place extends BaseEntity{


    private Integer placeNumber;
    private Boolean isVip;
    private Double priceCoefficient;
    private Row row;

    @Column(name = "place_number")
    public Integer getPlaceNumber() {
        return placeNumber;
    }

    public void setPlaceNumber(Integer placeNumber) {
        this.placeNumber = placeNumber;
    }

    @Column(name = "is_vip")
    public Boolean getIsVip() {
        return isVip;
    }

    public void setIsVip(Boolean VIP) {
        isVip = VIP;
    }

    @Column(name = "price_coefficient")
    public Double getPriceCoefficient() {
        return priceCoefficient;
    }

    public void setPriceCoefficient(Double priceCoefficient) {
        this.priceCoefficient = priceCoefficient;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "row_id")
    public Row getRow() {
        return row;
    }

    public void setRow(Row row) {
        this.row = row;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id = " +getId() +
                ", placeNumber=" + placeNumber +
                ", isVip=" + isVip +
                ", priceCoefficient=" + priceCoefficient +
//                ", row=" + row +
                '}';
    }
}
