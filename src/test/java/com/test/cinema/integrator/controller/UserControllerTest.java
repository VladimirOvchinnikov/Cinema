package com.test.cinema.integrator.controller;

import com.test.cinema.exception.ExceptionResponse;
import com.test.cinema.integrator.util.Function;
import com.test.cinema.integrator.util.GenerateData;
import com.test.cinema.model.entity.User;
import com.test.cinema.model.request.UserRequestBean;
import com.test.cinema.model.response.UserResponseBean;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.test.cinema.util.Constant.FILTER_NAME_PAGE;
import static com.test.cinema.util.Constant.FILTER_NAME_PAGE_DEFAULT_VALUE;
import static com.test.cinema.util.Constant.FILTER_NAME_PER_PAGE;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends BaseControllerTest<User, UserRequestBean, UserResponseBean> {

    private User postUser;
    private User putUser;


    private void init() {
        List<User> users = new ArrayList<>();
        Integer uId = GenerateData.getLastId("users");
        for (int i = 0; i < countEntity; i++) {
            User user = new User();
            user.setId(uId--);
            user.setFirstName("Имя " + i);
            user.setMiddleName("Отчество " + i);
            user.setLastName("Фамилия " + i);
            user.setPassword("password " + i);
            user.setLogin("login " + i);
            user.setBirthday(LocalDate.of(1991 + i, 10, 6));
            users.add(user);
        }
        entities = GenerateData.generateUsers(users, false);

        postUser = new User();
        postUser.setId(null);
        postUser.setFirstName("Имя post");
        postUser.setMiddleName("Отчество post");
        postUser.setLastName("Фамилия post");
        postUser.setPassword("password post");
        postUser.setLogin("login post");
        postUser.setBirthday(LocalDate.of(1989, 10, 6));

        putUser = new User();
        User u = entities.get(0);
        putUser.setId(u.getId());
        putUser.setFirstName(u.getFirstName() + " put");
        putUser.setMiddleName(u.getMiddleName() + " put");
        putUser.setLastName(u.getLastName() + " put");
        putUser.setPassword(u.getPassword() + " put");
        putUser.setLogin(u.getLogin() + " put");
        putUser.setBirthday(LocalDate.of(2007, 6, 10));
    }

    @Override
    protected void prepareTestData() throws Exception {
        super.prepareTestData();
        path = "/users";
        init();
    }

    @Override
    protected void dropTestData() throws Exception {
        super.dropTestData();
        GenerateData.dropData();

    }

    @Test
    public void testPostUser() throws Exception {
        ResultActions ra = post(entityToRequestBean(postUser))
                .andExpect(status().isCreated());
        analiseResponse(ra, entityToResponseBean(postUser), null);
    }

    @Test
    public void testFailPostUser() throws Exception {
        validateTypicalException(entityToRequestBean(postUser), this::post);
    }

    private void validateTypicalException(UserRequestBean bean, Function func) throws Exception {
        ExceptionResponse er = new ExceptionResponse();
        er.setCode(HttpStatus.BAD_REQUEST.value());

        bean.setFirstName(null);
        er.setMessage(messageService.getMessage("user.error.not-found.first-name"));
        exceptionResponse(func.exec(bean), er);
        bean.setFirstName("");
        exceptionResponse(func.exec(bean), er);
        bean.setFirstName(postUser.getFirstName());

        bean.setMiddleName(null);
        er.setMessage(messageService.getMessage("user.error.not-found.middle-name"));
        exceptionResponse(func.exec(bean), er);
        bean.setMiddleName("");
        exceptionResponse(func.exec(bean), er);
        bean.setMiddleName(postUser.getMiddleName());

        bean.setLastName(null);
        er.setMessage(messageService.getMessage("user.error.not-found.last-name"));
        exceptionResponse(func.exec(bean), er);
        bean.setLastName("");
        exceptionResponse(func.exec(bean), er);
        bean.setLastName(postUser.getLastName());

        bean.setLogin(null);
        er.setMessage(messageService.getMessage("user.error.not-found.login"));
        exceptionResponse(func.exec(bean), er);
        bean.setLogin("");
        exceptionResponse(func.exec(bean), er);
        bean.setLogin(postUser.getLogin());

        bean.setPassword(null);
        er.setMessage(messageService.getMessage("user.error.not-found.password"));
        exceptionResponse(func.exec(bean), er);
        bean.setPassword("");
        exceptionResponse(func.exec(bean), er);
        bean.setPassword(postUser.getPassword());

        bean.setBirthday(null);
        er.setMessage(messageService.getMessage("user.error.not-found.birthday"));
        exceptionResponse(func.exec(bean), er);
        bean.setBirthday(postUser.getBirthday());

    }

    @Test
    public void testPutUser() throws Exception {
        ResultActions ra = put(entityToRequestBean(putUser)).andExpect(status().isOk());
        analiseResponse(ra, entityToResponseBean(putUser), null);
    }

    @Test
    public void testFailPutUser() throws Exception {
        UserRequestBean bean = entityToRequestBean(putUser);
        validateTypicalException(bean, this::put);

        bean.setId(GenerateData.getLastId("users"));
        ExceptionResponse er = new ExceptionResponse();
        er.setCode(HttpStatus.BAD_REQUEST.value());
        er.setMessage(messageService.getMessage("user.error.not-found", bean.getId()));
        exceptionResponse(put(bean), er);
        bean.setId(putUser.getId());
    }

    @Test
    public void testGetFilm() throws Exception {
        get();

        putMapFilter(FILTER_NAME_PAGE, "2");
        get();

        putMapFilter(FILTER_NAME_PAGE, FILTER_NAME_PAGE_DEFAULT_VALUE);
        putMapFilter(FILTER_NAME_PER_PAGE, "1");
        get();

        putMapFilter(FILTER_NAME_PER_PAGE, "2");
        get();

        putMapFilter(FILTER_NAME_PAGE, "2");
        get();
    }

    @Test
    public void testGetEmptyFilms() throws Exception {
        dropTestData();
        countEntity=0;
        get();
    }

    @Test
    public void testGetByIdFilm() throws Exception {
        User user = entities.get(0);
        analiseResponse(get(user.getId()), entityToResponseBean(user), null);

        analiseResponse(get(GenerateData.getLastId("users")), null, null);
    }

    @Test
    public void testDeleteFilm() throws Exception {
        delete(entities.get(0).getId());
    }

    @Override
    public void analiseResponse(ResultActions ra, UserResponseBean bean, Integer index) throws Exception {
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
        ra = ra.andExpect(jsonPath(prePath + ".first_name", is(bean.getFirstName())))
                .andExpect(jsonPath(prePath + ".middle_name", is(bean.getMiddleName())))
                .andExpect(jsonPath(prePath + ".last_name", is(bean.getLastName())))
                .andExpect(jsonPath(prePath + ".birthday", is(getDate(bean.getBirthday()))))
                .andExpect(jsonPath(prePath + ".login", is(bean.getLogin())))
                .andExpect(jsonPath(prePath + ".password", is(bean.getPassword())))
                .andExpect(jsonPath(prePath + ".is_authorization", is(bean.getIsAuthorization())));
    }

    @Override
    public UserRequestBean entityToRequestBean(User entity) {
        UserRequestBean bean = new UserRequestBean();
        bean.setId(entity.getId());
        bean.setFirstName(entity.getFirstName());
        bean.setMiddleName(entity.getMiddleName());
        bean.setLastName(entity.getLastName());
        bean.setLogin(entity.getLogin());
        bean.setPassword(entity.getPassword());
        bean.setBirthday(entity.getBirthday());
        return bean;
    }

    @Override
    public UserResponseBean entityToResponseBean(User entity) {
        UserResponseBean bean = new UserResponseBean();
        bean.setId(entity.getId());
        bean.setCreatedAt(entity.getCreatedAt());
        bean.setFirstName(entity.getFirstName());
        bean.setMiddleName(entity.getMiddleName());
        bean.setLastName(entity.getLastName());
        bean.setLogin(entity.getLogin());
        bean.setPassword(entity.getPassword());
        bean.setBirthday(entity.getBirthday());
        bean.setIsAuthorization(entity.getIsAuthorization());
        return bean;
    }

    @Override
    public User responseBeanToEntity(UserResponseBean bean) {
        return null;
    }
}
