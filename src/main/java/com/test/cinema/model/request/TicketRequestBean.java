package com.test.cinema.model.request;

public class TicketRequestBean implements RequestBean {

    private Integer id;
    private Integer seanceId;
    private Integer placeId;
    private Integer orderId;


    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSeanceId() {
        return seanceId;
    }

    public void setSeanceId(Integer seanceId) {
        this.seanceId = seanceId;
    }

    public Integer getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Integer placeId) {
        this.placeId = placeId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
