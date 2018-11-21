package com.test.cinema.model.entity;


import javax.persistence.*;

@Entity
@Table(name = "v_tickets")
public class Ticket extends BaseEntity {

    private Seance seance;
    private Place place;
    private Order order;
    private String code; // типо уникальный идентификатор но тот что виден людям

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seance_id", nullable = false)
    public Seance getSeance() {
        return seance;
    }

    public void setSeance(Seance seance) {
        this.seance = seance;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Column(name = "code", nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
