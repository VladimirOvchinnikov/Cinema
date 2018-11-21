package com.test.cinema.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import static com.test.cinema.util.Constant.PATTERN_DATE_TIME;

public class TicketResponseBean implements ResponseBean {

    private Integer id;
    private Integer seanceId;
    private Integer placeId;
    private Integer orderId;
    private String code;
    @JsonFormat(pattern = PATTERN_DATE_TIME)
    private LocalDateTime createdAt;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
