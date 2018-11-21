package com.test.cinema.repository;

import com.test.cinema.model.entity.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmRepository extends JpaRepository<Film, Integer> {

    @Modifying
    @Query("update Film set deletedAt = now() where id = :id")
    int delete(@Param("id") Integer id);

    @Query("select f from Film f where now() <= f.endOfHire ")
    Page<Film> selectFilms(Pageable pageable);

}
