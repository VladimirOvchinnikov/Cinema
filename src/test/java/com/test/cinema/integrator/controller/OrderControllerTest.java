package com.test.cinema.integrator.controller;

import com.test.cinema.exception.ExceptionResponse;
import com.test.cinema.integrator.util.Function;
import com.test.cinema.integrator.util.GenerateData;
import com.test.cinema.model.entity.*;
import com.test.cinema.model.request.OrderRequestBean;
import com.test.cinema.model.response.OrderResponseBean;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.test.cinema.util.Constant.FILTER_NAME_PAGE;
import static com.test.cinema.util.Constant.FILTER_NAME_PER_PAGE;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Ovchinnikov Vladimir email ovchinnikovvg@altarix.ru
 */
public class OrderControllerTest extends BaseControllerTest<Order, OrderRequestBean, OrderResponseBean> {


    private Order postOrder;
    private Order putOrder;

    private List<Ticket> tickets;
    private List<User> users;

    private Integer countUsers = 3;
    private Integer countSeance = 1;
    private Integer countRow = 3;
    private Integer countPlace = 4;


    private void init() {
        users = GenerateData.generateUsers(countUsers, false);

        List<Seance> seances = GenerateData.generateSeances(countSeance, countRow, countPlace, true);
        Integer sId = GenerateData.getLastId("seances");
        for (Seance s : seances) {
            s.setDateSeance(s.getDateSeance().plusDays(1));
            s.setId(sId--);
        }
        ;
        seances = GenerateData.generateSeances(seances, false);
        List<Ticket> tickets1 = new ArrayList<>();
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
                    tickets1.add(ticket);

                }
            }
        }

        tickets = GenerateData.generateTickets(tickets1, false);

        List<Order> orders = new ArrayList<>();
        Integer oId = GenerateData.getLastId("orders");
        Integer startInd = 0;
        Integer iU = 0;
        countEntity = 4;
        for (int i = 0; i < countEntity; i++) {
            Order order = new Order();
            order.setId(oId--);
            order.setBonusCount(i);
            order.setTicketCount(i);
            order.setTotalPrice(i * 100d);
            order.setUser(users.get(iU));
            iU++;
            if (Objects.equals(iU, countUsers)) {
                iU = 0;
            }
            order.setTickets(tickets.subList(startInd, startInd + 2));
            order.setTicketCount(order.getTickets().size());
            startInd = startInd + 3;
            orders.add(order);
        }

        postOrder = new Order();
        Order o = orders.get(0);
        postOrder.setBonusCount(o.getBonusCount());
        postOrder.setTicketCount(o.getTicketCount());
        postOrder.setTotalPrice(o.getTotalPrice());
        postOrder.setUser(o.getUser());
        postOrder.setTickets(o.getTickets());

        orders = orders.subList(1, orders.size());

        entities = GenerateData.generateOrders(orders, false);
        putOrder = new Order();
        o = entities.get(0);
        putOrder.setId(o.getId());
        putOrder.setTotalPrice(o.getTotalPrice() + 1);
        List<Ticket> ts = new ArrayList<>();
        Ticket t = tickets1.get(0);

        Ticket ticket = new Ticket();
        ticket.setId(GenerateData.getLastId("tickets"));
        ticket.setSeance(t.getSeance());
        ticket.setPlace(t.getPlace());
        ticket.setOrder(t.getOrder());
        ts.add(ticket);
        ts = GenerateData.generateTickets(ts, false);

        ts.addAll(o.getTickets().subList(0, 1));
        putOrder.setTickets(ts);
        putOrder.setTicketCount(putOrder.getTickets().size());
        putOrder.setBonusCount(o.getBonusCount() + 1);
        putOrder.setUser(o.getUser());
        countEntity =entities.size();
    }

    @Override
    protected void prepareTestData() throws Exception {
        super.prepareTestData();
        path = "/orders";
        init();
    }

    @Override
    protected void dropTestData() throws Exception {
        super.dropTestData();
        GenerateData.dropData();
    }

    @Test
    public void testPostOrders() throws Exception {
        ResultActions ra = post(entityToRequestBean(postOrder))
                .andExpect(status().isCreated());
        analiseResponse(ra, entityToResponseBean(postOrder), null);
    }

    @Test
    public void testFailPostOrders() throws Exception {
        validateTypicalException(entityToRequestBean(postOrder), this::post);
    }

    @Test
    public void testPutOrders() throws Exception {
        Order order = entities.get(0);
        ResultActions ra = put(entityToRequestBean(order))
                .andExpect(status().isOk());
        analiseResponse(ra, entityToResponseBean(order), null);

        ra = put(entityToRequestBean(putOrder))
                .andExpect(status().isOk());
        analiseResponse(ra, entityToResponseBean(putOrder), null);
    }

    @Test
    public void testFailPutOrders() throws Exception {
        OrderRequestBean bean = entityToRequestBean(putOrder);
        validateTypicalException(bean, this::put);

        bean.setId(GenerateData.getLastId("orders"));
        ExceptionResponse er = new ExceptionResponse();
        er.setCode(HttpStatus.BAD_REQUEST.value());
        er.setMessage(messageService.getMessage("order.error.not-found", bean.getId()));
        exceptionResponse(put(bean), er);
        bean.setId(putOrder.getId());
    }

    @Test
    public void testDeleteOrders() throws Exception{
        Order order = entities.get(0);
        List<Ticket> tickets = order.getTickets();
        delete(order.getId());

        for (Ticket ticket: tickets) {
            MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/tickets/" + ticket.getId());
            get = get.contentType(contentType);
            ResultActions ra = getMockMvc().perform(get)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(ticket.getId())))
                    .andExpect(jsonPath("$.order_id", nullValue()));
        }
    }

    @Test
    public void testFailDeleteOrder() throws Exception{
        Order order = new Order();
        order.setUser(GenerateData.generateUsers(1,false).get(0));
        order.setId(GenerateData.getLastId("orders"));
        order.setBonusCount(0);
        order.setTicketCount(1);
        order.setTotalPrice(1D);
        Ticket ticket = new Ticket();
        ticket.setId(GenerateData.getLastId("tickets"));

        Seance seance = GenerateData.generateSeances(1,1,1,true).get(0);
        seance.setDateSeance(LocalDate.now());
        seance.setTimeSeance(LocalTime.now().plusMinutes(60));
        seance.setId(GenerateData.getLastId("seances"));
        ticket.setSeance(GenerateData.generateSeances(Collections.singletonList(seance), false).get(0));
        ticket.setPlace(seance.getHall().getRows().get(0).getPlaces().get(0));
        order.setTickets(GenerateData.generateTickets(Collections.singletonList(ticket),false));
        order = GenerateData.generateOrders(Collections.singletonList(order), false).get(0);



        Integer id = order.getId();
        ExceptionResponse er = new ExceptionResponse();
        er.setCode(HttpStatus.BAD_REQUEST.value());
        er.setMessage(messageService.getMessage("order.error.invalid.not-delete", id));
        MockHttpServletRequestBuilder delete = MockMvcRequestBuilders.delete(path + "/" + id);
        delete = delete.contentType(contentType);
        ResultActions ra = getMockMvc().perform(delete)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(er.getCode())))
                .andExpect(jsonPath("$.message", is(er.getMessage())));

    }

    @Test
    public void testGetOrders() throws Exception{
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
    public void testGetByIdOrder() throws Exception{
        Order order = entities.get(0);
        ResultActions ra = get(order.getId());
        analiseResponse(ra, entityToResponseBean(order), null);

        ra = get(GenerateData.getLastId("orders"));
        analiseResponse(ra, null, null);
    }


    private void validateTypicalException(OrderRequestBean bean, Function func) throws Exception {
        ExceptionResponse er = new ExceptionResponse();
        er.setCode(HttpStatus.BAD_REQUEST.value());

        bean.setUserId(null);
        er.setMessage(messageService.getMessage("order.error.not-found.user"));
        exceptionResponse(func.exec(bean), er);
        bean.setUserId(GenerateData.getLastId("users"));
        er.setMessage(messageService.getMessage("order.error.not-found.user"));
        exceptionResponse(func.exec(bean), er);
        bean.setUserId(postOrder.getUser().getId());

        bean.setTicketIds(null);
        er.setMessage(messageService.getMessage("order.error.invalid.zero-ticket"));
        exceptionResponse(func.exec(bean), er);
        bean.setTicketIds(new ArrayList<>());
        exceptionResponse(func.exec(bean), er);
        bean.setTicketIds(postOrder.getTickets().stream().map(BaseEntity::getId).collect(Collectors.toList()));

        List<Integer> ticketIds = new ArrayList<>();
        ticketIds.add(null);
        bean.setTicketIds(ticketIds);
        er.setMessage(messageService.getMessage("order.error.not-found.ticket"));
        exceptionResponse(func.exec(bean), er);
        ticketIds.add(null);
        exceptionResponse(func.exec(bean), er);
        ticketIds.clear();
        ticketIds.add(postOrder.getTickets().get(0).getId());
        ticketIds.add(null);
        bean.setTicketIds(ticketIds);
        exceptionResponse(func.exec(bean), er);
        ticketIds.clear();
        ticketIds.add(GenerateData.getLastId("tickets"));
        bean.setTicketIds(ticketIds);
        exceptionResponse(func.exec(bean), er);
        ticketIds.clear();
        ticketIds.add(postOrder.getTickets().get(0).getId());
        ticketIds.add(GenerateData.getLastId("tickets"));
        bean.setTicketIds(ticketIds);
        exceptionResponse(func.exec(bean), er);
        bean.setTicketIds(postOrder.getTickets().stream().map(BaseEntity::getId).collect(Collectors.toList()));

        bean.setTicketIds(entities.get(1).getTickets().stream().map(BaseEntity::getId).collect(Collectors.toList()));
        er.setMessage(messageService.getMessage("order.error.invalid.ticket"));
        exceptionResponse(func.exec(bean), er);
        ticketIds.clear();
        ticketIds.addAll(postOrder.getTickets().stream().map(BaseEntity::getId).collect(Collectors.toList()));
        ticketIds.add(entities.get(0).getTickets().get(0).getId());
        bean.setTicketIds(ticketIds);
        bean.setTicketIds(postOrder.getTickets().stream().map(BaseEntity::getId).collect(Collectors.toList()));

    }


    @Override
    public void analiseResponse(ResultActions ra, OrderResponseBean bean, Integer index) throws Exception {
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
        ra = ra.andExpect(jsonPath(prePath + ".bonus_count", is(bean.getBonusCount())))
                .andExpect(jsonPath(prePath + ".ticket_count", is(bean.getTicketCount())))
                .andExpect(jsonPath(prePath + ".total_price", is(bean.getTotalPrice())))
                .andExpect(jsonPath(prePath + ".user_id", is(bean.getUserId())))
                .andExpect(jsonPath(prePath + ".ticket_ids", hasSize(bean.getTicketIds().size())));
        for (int i = 0; i < bean.getTicketIds().size(); i++) {
            ra = ra.andExpect(jsonPath(prePath + ".ticket_ids[" + i + "]", is(bean.getTicketIds().get(i))));
        }


    }

    @Override
    public OrderRequestBean entityToRequestBean(Order entity) {
        OrderRequestBean bean = new OrderRequestBean();
        bean.setId(entity.getId());
        bean.setBonusCount(entity.getBonusCount());
        bean.setTicketCount(entity.getTicketCount());
        bean.setTotalPrice(entity.getTotalPrice());
        bean.setTicketIds(entity.getTickets().stream().map(BaseEntity::getId).collect(Collectors.toList()));
        bean.setUserId(entity.getUser().getId());
        return bean;
    }

    @Override
    public OrderResponseBean entityToResponseBean(Order entity) {
        OrderResponseBean bean = new OrderResponseBean();
        bean.setId(entity.getId());
        bean.setBonusCount(entity.getBonusCount());
        bean.setCreatedAt(entity.getCreatedAt());
        bean.setTicketCount(entity.getTicketCount());
        bean.setTotalPrice(entity.getTotalPrice());
        bean.setUserId(entity.getUser().getId());
        bean.setTicketIds(entity.getTickets().stream().map(BaseEntity::getId).collect(Collectors.toList()));
        return bean;
    }

    @Override
    public Order responseBeanToEntity(OrderResponseBean bean) {
        return null;
    }
}
