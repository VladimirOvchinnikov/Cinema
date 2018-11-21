package com.test.cinema.repository;

import com.test.cinema.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Modifying
    @Query("update Order set deletedAt = now() where id = :id")
    void delete(@Param("id") Integer id);
}
