package com.test.cinema.model.request;

import java.util.ArrayList;
import java.util.List;

public class HallRequestBean implements RequestBean {

    private Integer id;
    private String name;
    private List<RowRequestBean> rows = new ArrayList<>();

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

    public List<RowRequestBean> getRows() {
        return rows;
    }

    public void setRows(List<RowRequestBean> rows) {
        this.rows = rows;
    }
}
