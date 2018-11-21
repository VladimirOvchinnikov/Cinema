package com.test.cinema.integrator.controller;

import com.test.cinema.exception.ExceptionResponse;
import com.test.cinema.integrator.util.Function;
import com.test.cinema.integrator.util.GenerateData;
import com.test.cinema.model.entity.*;
import com.test.cinema.model.request.TicketRequestBean;
import com.test.cinema.model.response.TicketResponseBean;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.test.cinema.util.Constant.FILTER_NAME_PAGE;
import static com.test.cinema.util.Constant.FILTER_NAME_PER_PAGE;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TicketControllerTest extends BaseControllerTest<Ticket, TicketRequestBean, TicketResponseBean> {

    private Ticket postTicket;
    private Ticket putTicket;

    private List<Ticket> postTickets;

    private List<Seance> seances;
    private Integer countSeance = 1;
    private Integer countRow = 3;
    private Integer countPlace = 3;

    private List<Place> places;

//    private List<User> users;
//    private Integer countUser = 3;

    private void init() {

        seances = GenerateData.generateSeances(countSeance, countRow, countPlace, false);
//        users = GenerateData.generateUsers(countUser, false);
        List<Ticket> tickets = new ArrayList<>();
        Integer tId = GenerateData.getLastId("tickets");
        for (int iS = 0; iS < countSeance; iS++) {
            Seance seance = seances.get(iS);
            for (int iR = 0; iR < countRow; iR++) {
                Row row = seance.getHall().getRows().get(iR);
                for (int iP = 0; iP < countPlace; iP++) {
                    Place place = row.getPlaces().get(iP);
                    Ticket ticket = new Ticket();
                    ticket.setId(tId--);
                    ticket.setSeance(seance);
                    ticket.setPlace(place);
                    ticket.setOrder(new Order());
                    tickets.add(ticket);

                }
            }
        }

//        int iU = 0;
//        for (Ticket ticket: tickets) {
//            if (iU < countUser) {
//                ticket.setOrder(users.get(iU++));
//            }else {
//                iU = 0;
//                ticket.setOrder(users.get(iU++));
//            }
//        }


        postTickets = tickets.stream().skip(tickets.size()>>1).collect(Collectors.toList());
        postTickets.forEach(t->t.setId(null));
        entities = tickets.subList(0,(tickets.size()>>1));

        entities = GenerateData.generateTickets(entities, false);


        postTicket = postTickets.get(0);
        putTicket = entities.get(0);

        countEntity = entities.size();

    }

    @Override
    protected void prepareTestData() throws Exception {
        super.prepareTestData();
        path = "/tickets";
        init();


    }

    @Override
    protected void dropTestData() throws Exception {
        super.dropTestData();
        GenerateData.dropData();
    }

    @Test
    public void testGetTickets() throws Exception {
        ResultActions ra = get();

        putMapFilter(FILTER_NAME_PER_PAGE, "1");
        ra = get();

        putMapFilter(FILTER_NAME_PAGE, "2");
        ra = get();

        putMapFilter(FILTER_NAME_PAGE, "3");
        ra = get();

        putMapFilter(FILTER_NAME_PAGE, "4");
        ra = get();
    }

    @Test
    public void testPostTickets() throws Exception {
        ResultActions ra = post(entityToRequestBean(postTicket))
                .andExpect(status().isCreated());
        analiseResponse(ra, entityToResponseBean(postTicket), null);
    }


    @Test
    public void testFailPostTickets() throws Exception {
        validateTypicalException(entityToRequestBean(postTickets.get(0)), this::post);
    }

    private void validateTypicalException(TicketRequestBean bean, Function func) throws Exception {
        ExceptionResponse er = new ExceptionResponse();
        er.setCode(HttpStatus.BAD_REQUEST.value());

        bean.setSeanceId(null);
        er.setMessage(messageService.getMessage("ticket.error.not-found.seance"));
        exceptionResponse(func.exec(bean), er);
        bean.setSeanceId(GenerateData.getLastId("seances"));
        er.setMessage(messageService.getMessage("seance.error.not-found", bean.getSeanceId()));
        exceptionResponse(func.exec(bean), er);
        bean.setSeanceId(postTicket.getSeance().getId());

        bean.setPlaceId(null);
        er.setMessage(messageService.getMessage("ticket.error.not-found.place"));
        exceptionResponse(func.exec(bean), er);
        bean.setPlaceId(GenerateData.getLastId("places"));
        er.setMessage(messageService.getMessage("ticket.error.not-found.place"));
        exceptionResponse(func.exec(bean), er);
        bean.setPlaceId(postTicket.getPlace().getId());

        Integer idT = GenerateData.getLastId("tickets");
        for (Ticket ticket: postTickets){
            ticket.setId(idT--);
        }
        GenerateData.generateTickets(postTickets, false);
        er.setMessage(messageService.getMessage("ticket.error.invalid-count"));
        exceptionResponse(func.exec(bean), er);
    }

    @Override
    public void analiseResponse(ResultActions ra, TicketResponseBean bean, Integer index) throws Exception {
        if (bean == null) {
            ra.andExpect(content().string(isEmptyOrNullString()));
            return;
        }
        String prePath = "$" + (index != null ? "[" + index + "]" : "");
        if (bean.getId() == null) {
            ra = ra.andExpect(jsonPath(prePath + ".id", notNullValue()));
        } else {
            ra = ra.andExpect(jsonPath(prePath + ".id", is(bean.getId())));
        }
        if (bean.getCreatedAt() != null) {
            ra = ra.andExpect(jsonPath(prePath + ".created_at", is(getDateTime(bean.getCreatedAt()))));
        } else {
            ra = ra.andExpect(jsonPath(prePath + ".created_at", notNullValue(LocalDateTime.class)));
        }
        ra = ra.andExpect(jsonPath(prePath + ".seance_id", is(bean.getSeanceId())))
                .andExpect(jsonPath(prePath + ".place_id", is(bean.getPlaceId())))
                .andExpect(jsonPath(prePath + ".code", is(bean.getCode())));
        if (bean.getOrderId() == null){
            ra = ra.andExpect(jsonPath(prePath + ".order_id", nullValue(Integer.class)));
        }else {
            ra = ra.andExpect(jsonPath(prePath + ".order_id", is(bean.getOrderId())));
        }
    }

    @Override
    public TicketRequestBean entityToRequestBean(Ticket entity) {
        TicketRequestBean bean = new TicketRequestBean();
        bean.setId(entity.getId());
        bean.setSeanceId(entity.getSeance().getId());
        bean.setPlaceId(entity.getPlace().getId());
        bean.setOrderId(entity.getOrder().getId());
        return bean;

    }

    @Override
    public TicketResponseBean entityToResponseBean(Ticket entity) {
        TicketResponseBean bean = new TicketResponseBean();
        bean.setId(entity.getId());
        bean.setCreatedAt(entity.getCreatedAt());
        bean.setCode(entity.getCode());
        bean.setSeanceId(entity.getSeance().getId());
        bean.setPlaceId(entity.getPlace().getId());
        bean.setOrderId(entity.getOrder().getId());
        return bean;
    }

    @Override
    public Ticket responseBeanToEntity(TicketResponseBean bean) {
        return null;
    }
}
