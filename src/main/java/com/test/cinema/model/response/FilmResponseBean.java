package com.test.cinema.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.test.cinema.util.Constant.PATTERN_DATE;
import static com.test.cinema.util.Constant.PATTERN_DATE_TIME;
import static com.test.cinema.util.Constant.PATTERN_TIME;

public class FilmResponseBean implements ResponseBean {

    private Integer id;
    private String name;
    private String poster;
    @JsonFormat(pattern = PATTERN_TIME)
    private LocalTime duration;
    private Double price;
    @JsonFormat(pattern = PATTERN_DATE)
    private LocalDate startOfHire;
    @JsonFormat(pattern = PATTERN_DATE)
    private LocalDate endOfHire;
    @JsonFormat(pattern = PATTERN_DATE_TIME)
    private LocalDateTime createdAt;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public LocalTime getDuration() {
        return duration;
    }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDate getStartOfHire() {
        return startOfHire;
    }

    public void setStartOfHire(LocalDate startOfHire) {
        this.startOfHire = startOfHire;
    }

    public LocalDate getEndOfHire() {
        return endOfHire;
    }

    public void setEndOfHire(LocalDate endOfHire) {
        this.endOfHire = endOfHire;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
