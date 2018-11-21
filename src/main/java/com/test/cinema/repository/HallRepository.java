package com.test.cinema.repository;

import com.test.cinema.model.entity.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HallRepository extends JpaRepository<Hall, Integer> {

    @Modifying
    @Query("update Hall set deletedAt = now() where id = :id")
    public int delete(@Param("id") Integer id);

}
