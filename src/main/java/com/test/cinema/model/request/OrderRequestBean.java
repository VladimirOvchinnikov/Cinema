package com.test.cinema.model.request;

import java.util.List;

public class OrderRequestBean implements RequestBean {

    private Integer id;
    private Integer userId;
    private Integer ticketCount;
    private Integer bonusCount;
    private Double totalPrice;
    private List<Integer> ticketIds;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(Integer ticketCount) {
        this.ticketCount = ticketCount;
    }

    public Integer getBonusCount() {
        return bonusCount;
    }

    public void setBonusCount(Integer bonusCount) {
        this.bonusCount = bonusCount;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<Integer> getTicketIds() {
        return ticketIds;
    }

    public void setTicketIds(List<Integer> ticketIds) {
        this.ticketIds = ticketIds;
    }
}
