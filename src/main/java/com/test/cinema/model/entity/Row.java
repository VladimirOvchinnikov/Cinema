package com.test.cinema.model.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "v_rows")
public class Row extends BaseEntity{

    private Integer rowNumber;
    private Hall hall;
    private List<Place> places;

    @Column(name = "row_number")
    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

//    @OneToMany(mappedBy = "row", fetch = FetchType.LAZY)
    @Transient
    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    @Override
    public String toString() {
        return "Row{" +
                "id = " +getId() +
                ", rowNumber=" + rowNumber +
//                ", hall=" + hall +
                ", places=" + places +
                '}';
    }
}
