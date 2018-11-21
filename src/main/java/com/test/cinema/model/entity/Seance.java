package com.test.cinema.model.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "v_seances")
public class Seance extends BaseEntity {
    private Film film;
    private Hall hall;
    private LocalDate dateSeance;
    private LocalTime timeSeance;
//    private Double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", nullable = false)
    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id", nullable = false)
    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    @Column(name = "date_seance", nullable = false)
    public LocalDate getDateSeance() {
        return dateSeance;
    }

    public void setDateSeance(LocalDate dateSeance) {
        this.dateSeance = dateSeance;
    }

    @Column(name = "time_seance", nullable = false)
    public LocalTime getTimeSeance() {
        return timeSeance;
    }

    public void setTimeSeance(LocalTime timeSeance) {
        this.timeSeance = timeSeance;
    }


//    public Double getPrice() {
//        return price;
//    }
//
//    public void setPrice(Double price) {
//        this.price = price;
//    }
}
