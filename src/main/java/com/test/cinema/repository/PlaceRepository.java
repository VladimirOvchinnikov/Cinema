package com.test.cinema.repository;

import com.test.cinema.model.entity.Place;
import com.test.cinema.model.entity.Row;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface PlaceRepository extends JpaRepository<Place, Integer> {

    @Query("select distinct p from Place p where p.row= :row order by p.placeNumber, p.createdAt")
    List<Place> getPlacesByRow(@Param("row") Row row);

    @Modifying
    @Query("update Place p set p.deletedAt = now() where p in (select p1 from Place p1 where p1.row.hall.id = :hallId)")
//    @Query("update Place p set p.deletedAt = now() where p.row.hall.id = :hallId ")
    void deleteByHallId(@Param("hallId") Integer hallId);

    @Modifying
    @Query("update Place p set p.deletedAt = now() where p in (:places)")
    void deletePlaces(@Param("places") List<Place> places);
}
