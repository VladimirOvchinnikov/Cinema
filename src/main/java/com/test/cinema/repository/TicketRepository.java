package com.test.cinema.repository;

import com.test.cinema.model.entity.Order;
import com.test.cinema.model.entity.Seance;
import com.test.cinema.model.entity.Ticket;
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
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    @Query("select t from Ticket t where t.seance.id = :seanceId")
    List<Ticket> getTicketsBySeanceId(@Param("seanceId") Integer seanceId,
                                      Pageable pageable);

    @Query("select count(t) from Ticket t where t.seance=:seance")
    Integer getCountTicketBySeance(@Param("seance") Seance seance
//            , Pageable pageable
    );

    @Modifying
    @Query("update Ticket t \n" +
            "set t.order = :order \n" +
            "where t in (:tickets)")
    void buyTickets(@Param("tickets") List<Ticket> tickets,@Param("order") Order order);

    @Modifying
    @Query("update Ticket t \n" +
            "set t.order = null \n" +
            "where t in (:tickets) \n" +
            "   and t.order = :order ")
    void cancelTickets(@Param("tickets") List<Ticket> tickets, @Param("order") Order order);

    @Modifying
    @Query("update Ticket t \n" +
            "set t.order = null \n" +
            "where t.order.id = :orderId ")
    void cancelTicketsByOrder(@Param("orderId") Integer orderId);

    @Query("select count(t)>0 from Ticket t where t.seance.id=:seanceId and t.order is not null")
    boolean checkBuyTickets(@Param("seanceId") Integer seanceId);

    @Query("select t.seance.dateSeance, t.seance.timeSeance \n" +
            "from Ticket t \n" +
            "where t.order.id = :orderId \n" +
            "   and t.seance.dateSeance = current_date \n" +
            "group by t.seance.dateSeance, t.seance.timeSeance")
    Object[][] checkNotCancelOrder(@Param("orderId") Integer orderId);
}
