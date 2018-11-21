package com.test.cinema.repository;

import com.test.cinema.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<User, Integer> {
}
