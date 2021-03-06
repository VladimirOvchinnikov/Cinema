package com.test.cinema.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.test.cinema.util.Constant.PATTERN_DATE;
import static com.test.cinema.util.Constant.PATTERN_DATE_TIME;
import static com.test.cinema.util.Constant.PATTERN_TIME;

public class SeanceResponseBean implements ResponseBean{

    private Integer id;
    private Integer filmId;
    private Integer hallId;
    @JsonFormat(pattern = PATTERN_DATE)
    private LocalDate dateSeance;
    @JsonFormat(pattern = PATTERN_TIME)
    private LocalTime timeSeance;
    private Double price;
    @JsonFormat(pattern = PATTERN_DATE_TIME)
    private LocalDateTime createdAt;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFilmId() {
        return filmId;
    }

    public void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }

    public Integer getHallId() {
        return hallId;
    }

    public void setHallId(Integer hallId) {
        this.hallId = hallId;
    }

    public LocalDate getDateSeance() {
        return dateSeance;
    }

    public void setDateSeance(LocalDate dateSeance) {
        this.dateSeance = dateSeance;
    }

    public LocalTime getTimeSeance() {
        return timeSeance;
    }

    public void setTimeSeance(LocalTime timeSeance) {
        this.timeSeance = timeSeance;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
