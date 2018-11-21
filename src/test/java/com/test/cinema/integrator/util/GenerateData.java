package com.test.cinema.integrator.util;

import com.test.cinema.model.entity.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class GenerateData {

    //    @Autowired
    private static JdbcTemplate jdbcTemplate;

    public static void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        GenerateData.jdbcTemplate = jdbcTemplate;
    }

//    private static int countEntity;
//
//    public static void setCountEntity(int countEntity) {
//        GenerateData.countEntity = countEntity;
//    }

    public static List<Film> generateFilms(int count, boolean isNew) {
        Integer fId = getLastId("films");
        List<Film> films = new ArrayList<>();
        for (int i = 1; i < count + 1; i++) {
            Film film = new Film();
            if (!isNew) {
                film.setId(fId--);
            }
            film.setName("Фильм " + i);
            film.setPoster("Изображение " + i);
            film.setDuration(LocalTime.of(1, 30));
            film.setPrice(100.0);
            film.setStartOfHire(LocalDate.now().plusMonths(i));
            film.setEndOfHire(LocalDate.now().plusMonths(i + 1));
            films.add(film);
        }

        if (isNew) {
            return films;
        }
        for (Film film : films) {
            jdbcTemplate.execute(String.format(
                    "insert into films (id, name, poster, duration, price, start_of_hire, end_of_hire) values (%s, '%s', '%s', '%s', %s, '%s', '%s')",
                    film.getId(), film.getName(), film.getPoster(), film.getDuration(), film.getPrice(), film.getStartOfHire(), film.getEndOfHire()));
        }
        return films;
    }

    public static List<Hall> generateHalls(int countHall, int countRow, int countPlace, boolean isNew) {
        Integer pId = getLastId("places");
        Integer rId = getLastId("rows");
        Integer hId = getLastId("halls");
        List<Hall> halls = new ArrayList<>();
        for (int i = 1; i < countHall + 1; i++) {
            Hall hall = new Hall();
            if (!isNew) {
                hall.setId(hId--);
            }
            hall.setName("Тест Зал " + hall.getId());
            List<Row> rows = new ArrayList<>();
            for (int i1 = 1; i1 < countRow + 1; i1++) {
                Row row = new Row();
                row.setId(rId--);
                row.setHall(hall);
                row.setRowNumber(i1);
                List<Place> places = new ArrayList<>();
                for (int j = 1; j < countPlace + 1; j++) {
                    Place place = new Place();
                    place.setId(pId--);
                    place.setPlaceNumber(j);
                    place.setIsVip(j % 2 == 1);
                    place.setPriceCoefficient(place.getIsVip() ? 2.0 : 1.0);
                    place.setRow(row);
                    places.add(place);
                }
                row.setPlaces(places);
                rows.add(row);
            }
            hall.setRows(rows);
            halls.add(hall);
        }


        for (Hall hall : halls) {
            if (!isNew) {
                jdbcTemplate.execute(String.format("insert into halls (id, name) values (%s, '%s')", hall.getId(), hall.getName()));
            }

            for (Row row : hall.getRows()) {
                jdbcTemplate.execute(String.format("insert into rows (id, row_number, hall_id) values (%s, %s, %s)", row.getId(), row.getRowNumber(), hall.getId()));
                for (Place place : row.getPlaces()) {
                    jdbcTemplate.execute(String.format(
                            "insert into places (id, place_number, is_vip, price_coefficient, row_id) values (%s, %s, %s, %s, %s)",
                            place.getId(), place.getPlaceNumber(), place.getIsVip(), place.getPriceCoefficient(), row.getId()));
                }
            }
        }
        return halls;
    }

    public static List<Seance> generateSeances(int countSeance, int countRow, int countPlace, boolean isNew) {
        List<Film> films = generateFilms(countSeance, false);
        List<Hall> halls = generateHalls(countSeance, countRow, countPlace, false);
        List<Seance> seances = new ArrayList<>();

        Integer sId = getLastId("seances");

        for (int i = 1; i < countSeance + 1; i++) {
            Seance seance = new Seance();
            if (!isNew) {
                seance.setId(sId--);
            }

            seance.setFilm(films.get(i - 1));
            seance.setHall(halls.get(i - 1));
            seance.setDateSeance(LocalDate.now());
            seance.setTimeSeance(LocalTime.of(1, 30));
            seances.add(seance);
        }

        if (isNew) {
            return seances;
        }
        for (Seance seance : seances) {
            jdbcTemplate.execute(String.format(
                    "insert into seances (id, film_id, hall_id, date_seance, time_seance) values (%s, '%s', '%s', '%s', '%s')",
                    seance.getId(), seance.getFilm().getId(), seance.getHall().getId(), seance.getDateSeance(), seance.getTimeSeance()));
        }
        return seances;
    }

    public static List<Seance> generateSeances(List<Seance> seances, boolean isNew) {
        if (isNew) {
            return seances;
        }
        for (Seance seance : seances) {
            jdbcTemplate.execute(String.format(
                    "insert into seances (id, film_id, hall_id, date_seance, time_seance) values (%s, '%s', '%s', '%s', '%s')",
                    seance.getId(), seance.getFilm().getId(), seance.getHall().getId(), seance.getDateSeance(), seance.getTimeSeance()));
        }
        return seances;
    }

    public static List<Ticket> generateTickets(int countTicket, int countRow, int countPlace, boolean isNew) {
        Seance seance = generateSeances(1, countRow, countPlace, false).get(0);
//        List<User> users = generateUsers(countTicket, false);
        List<Ticket> tickets = new ArrayList<>();
        List<Place> places = new ArrayList<>();

        Integer tId = getLastId("tickets");

        for (Row row : seance.getHall().getRows()) {
            places.addAll(row.getPlaces());
        }

        for (int i = 1; i < countTicket + 1; i++) {
            Ticket ticket = new Ticket();
            if (!isNew) {
                ticket.setId(tId--);
            }
            ticket.setSeance(seance);
            ticket.setPlace(places.get(i - 1));
            ticket.setCode(String.valueOf(ticket.getPlace().getId()));
//            ticket.setOrder(users.get((int) (Math.random() * countTicket)));
            tickets.add(ticket);

        }
        if (isNew) {
            return tickets;
        }
        for (Ticket ticket : tickets) {
            jdbcTemplate.execute(String.format(
                    "insert into tickets (id, seance_id, place_id, order_id, code) values (%s, %s, %s, %s, %s)",
                    ticket.getId(), ticket.getSeance().getId(), ticket.getPlace().getId(), (ticket.getOrder()==null) ?null: ticket.getOrder().getId(), ticket.getCode()));
        }
        return tickets;
    }


    public static List<Ticket> generateTickets(List<Ticket> tickets, boolean isNew) {
        if (isNew) {
            return tickets;
        }
        for (Ticket ticket : tickets) {
            jdbcTemplate.execute(String.format(
                    "insert into tickets (id, seance_id, place_id, order_id, code) values (%s, %s, %s, %s, %s)",
                    ticket.getId(), ticket.getSeance().getId(), ticket.getPlace().getId(), (ticket.getOrder()==null) ?null: ticket.getOrder().getId(), ticket.getCode()));
        }
        return tickets;
    }


    public static List<User> generateUsers(int count, boolean isNew) {
        Integer uId = getLastId("users");
        List<User> users = new ArrayList<>();
        for (int i = 1; i < count + 1; i++) {
            User user = new User();
            if (!isNew) {
                user.setId(uId--);
            }
            users.add(user);
        }

        for (User user : users) {
            jdbcTemplate.execute(String.format(
                    "insert into users (id) values (%s)",
                    user.getId()));
        }
        return users;
    }

    public static List<User> generateUsers(List<User> users, boolean isNew) {
        if (isNew) {
            return users;
        }
        for (User user : users) {
            jdbcTemplate.execute(String.format(
                    "insert into users (id, first_name, middle_name, last_name, birthday, login, password) values (%s, '%s', '%s', '%s', '%s', '%s', '%s')",
                    user.getId(), user.getFirstName(), user.getMiddleName(), user.getLastName(), user.getBirthday(), user.getLogin(), user.getPassword()));
        }
        return users;
    }


    public static void dropData() {
        jdbcTemplate.execute("delete from tickets");
        jdbcTemplate.execute("delete from orders");
        jdbcTemplate.execute("delete from seances");
        jdbcTemplate.execute("delete from films");
        jdbcTemplate.execute("delete from places");
        jdbcTemplate.execute("delete from rows");
        jdbcTemplate.execute("delete from halls");
        jdbcTemplate.execute("delete from users");
    }


    public static Integer getLastId(String table) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select min(id) as id from " + table);
        Integer id = -1;
        if (rowSet.next()) {
            id = rowSet.getInt("id");
            id--;
        }

        return id;
    }

    public static List<Order> generateOrders(List<Order> orders, boolean isNew) {
        if (isNew) {
            return orders;
        }
        for (Order order : orders) {
            jdbcTemplate.execute(String.format(
                    "insert into orders (id, ticket_count, bonus_count, user_id, total_price) values (%s, %s, %s, %s, %s)",
                    order.getId(), order.getTicketCount(), order.getBonusCount(), order.getUser().getId(), order.getTotalPrice()));
            for (Ticket ticket : order.getTickets()) {
                jdbcTemplate.execute(String.format("update tickets set order_id = %s where id = %s ", order.getId(), ticket.getId()));
            }
        }
        return orders;
    }
}
