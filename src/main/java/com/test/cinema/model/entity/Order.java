package com.test.cinema.model.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "v_orders")
public class Order extends BaseEntity{


    private Integer ticketCount;
    private Integer bonusCount;
    private Double totalPrice;
    private User user;
    private List<Ticket> tickets;

    @Column(name = "ticket_count" )
    public Integer getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(Integer ticketCount) {
        this.ticketCount = ticketCount;
    }

    @Column(name = "bonus_count" )
    public Integer getBonusCount() {
        return bonusCount;
    }

    public void setBonusCount(Integer bonusCount) {
        this.bonusCount = bonusCount;
    }

    @Column(name = "total_price")
    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}
