package com.test.cinema.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

import static com.test.cinema.util.Constant.PATTERN_DATE_TIME;

public class HallResponseBean implements ResponseBean {

    private Integer id;
    private String name;
    private Integer rowCount;
    private List<RowResponseBean> rows;
    @JsonFormat(pattern = PATTERN_DATE_TIME)
    private LocalDateTime createdAt;

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

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public List<RowResponseBean> getRows() {
        return rows;
    }

    public void setRows(List<RowResponseBean> rows) {
        this.rows = rows;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
