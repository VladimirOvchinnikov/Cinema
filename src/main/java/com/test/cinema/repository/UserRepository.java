package com.test.cinema.repository;

import com.test.cinema.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Modifying
    @Query("update User set deletedAt = now() where id = :id")
    void delete(@Param("id") Integer id);
}
