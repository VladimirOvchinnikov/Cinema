package com.test.cinema.model.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "v_halls")
public class Hall extends BaseEntity {

    private String name;
    private List<Row> rows;

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transient
//    @OneToMany(mappedBy = "hall", fetch = FetchType.LAZY)
    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "Hall{" +
                "id = " +getId() +
                ", name='" + name + '\'' +
                ", rows=" + rows +
                '}';
    }
}
