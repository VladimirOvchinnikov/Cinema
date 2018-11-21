package com.test.cinema.model.entity;

import com.test.cinema.util.Interval;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "v_films")
@TypeDef(name="interval", typeClass = Interval.class)
public class Film extends BaseEntity{

    private String name;
    private String poster;
    private LocalTime duration;
    private Double price;
    private LocalDate startOfHire;
    private LocalDate endOfHire;

//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "duration")
    @Type(type = "interval")
    public LocalTime getDuration() {
        return duration;
    }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }

    @Column(name = "price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Column(name = "poster")
    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    @Column(name = "start_of_hire")
    public LocalDate getStartOfHire() {
        return startOfHire;
    }

    public void setStartOfHire(LocalDate startOfHire) {
        this.startOfHire = startOfHire;
    }

    @Column(name = "end_of_hire")
    public LocalDate getEndOfHire() {
        return endOfHire;
    }

    public void setEndOfHire(LocalDate endOfHire) {
        this.endOfHire = endOfHire;
    }
}
