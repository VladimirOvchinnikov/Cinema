package com.test.cinema.repository;

import com.test.cinema.model.entity.Hall;
import com.test.cinema.model.entity.Row;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RowRepository extends JpaRepository<Row, Integer> {
    @Query("select count(distinct r.id) from Row r where r.hall = :hall")
    Integer getRowCount(@Param("hall") Hall hall);

    @Query("select distinct r from Row r where r.hall = :hall order by  r.rowNumber, r.createdAt")
    List<Row> getRowsByHall(@Param("hall") Hall hall);

    @Modifying
    @Query("update Row r set r.deletedAt=now() where r.hall.id = :hallId")
    void deleteByHallId(@Param("hallId") Integer hallId);

    @Modifying
    @Query("update Row r set r.deletedAt=now() where r in (:rows)")
    void deleteRows(@Param("rows") List<Row> rows);
}
