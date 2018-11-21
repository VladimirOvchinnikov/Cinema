package com.test.cinema.repository;

import com.test.cinema.model.entity.Hall;
import com.test.cinema.model.entity.Seance;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface SeanceRepository extends JpaRepository<Seance, Integer> {

    @Query("select count(s)>0 \n" +
            "from Seance s \n" +
            "where (s.id != :id or :id is null) \n" +
            "   and s.hall = :hall\n" +
            "   and s.dateSeance = :dateSeance\n" +
            "   and :timeSeance between s.timeSeance and (s.timeSeance + s.film.duration)\n" +
            "")
    Boolean checkDateAndTimeSeance(
            @Param("id") Integer id,
            @Param("hall") Hall hall,
            @Param("dateSeance") LocalDate dateSeance,
            @Param("timeSeance") LocalTime timeSeance
//                                        ,Pageable pageable
    );

    @Query("select s \n" +
            "from Seance s \n" +
            "where (s.id != :id or :id is null) \n" +
            "   and s.hall = :hall\n" +
            "   and s.dateSeance = :dateSeance\n" +
            "   and :timeSeance between s.timeSeance and (s.timeSeance + s.film.duration)\n" +
            "")
    List<Seance> getSeanceByFilter(@Param("id") Integer id,
                                   @Param("hall") Hall hall,
                                   @Param("dateSeance") LocalDate dateSeance,
                                   @Param("timeSeance") LocalTime timeSeance,
                                   Pageable of);

    @Modifying
    @Query("update Seance set deletedAt = now() where id = :id")
    void delete(@Param("id") Integer id);

    @Query("select count(s)>0 from Seance s where s.film.id=:filmId ")
    boolean checkSeanceWithFilm(@Param("filmId") Integer filmId);

    @Query("select count(s)>0 from Seance s where s.hall.id=:hallId ")
    boolean checkActiveSeance(@Param("hallId") Integer hallId);
}
