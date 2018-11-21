package com.test.cinema.controller;

import com.test.cinema.model.entity.User;
import com.test.cinema.model.request.UserRequestBean;
import com.test.cinema.model.response.UserResponseBean;
import com.test.cinema.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.test.cinema.util.Constant.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<UserResponseBean> getList(@RequestParam(name = FILTER_NAME_PAGE, defaultValue = FILTER_NAME_PAGE_DEFAULT_VALUE) Integer page,
                                          @RequestParam(name = FILTER_NAME_PER_PAGE, defaultValue = FILTER_NAME_PER_PAGE_DEFAULT_VALUE) Integer perPage,
                                          HttpServletResponse response) {
        if (page < DEFAULT_MIN_PAGE) {
            page = DEFAULT_MIN_PAGE;
        }

        if (perPage < 0) {
            perPage = DEFAULT_PAGE_SIZE;
        } else if (perPage > DEFAULT_MAX_PAGE_SIZE) {
            perPage = DEFAULT_MAX_PAGE_SIZE;
        }

        Map<String, Object> filter = new HashMap<>();
        Page<User> users = service.getList(filter, page - 1, perPage);


        response.setHeader(HEADER_NAME_PAGES, String.valueOf(users.getTotalPages()));
        response.setHeader(HEADER_NAME_PAGE, String.valueOf(page));
        response.setHeader(HEADER_NAME_PAGE_SIZE, String.valueOf(perPage));
        return users.getContent().stream().map(this::entityToBean).collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public UserResponseBean getById(@PathVariable("id") Integer id){
        User user = service.getById(id);
        if (user == null){
            return null;
        }
        return entityToBean(user);
    }


    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseBean create(@RequestBody UserRequestBean bean){
        User user = beanToEntity(bean);
        user = service.create(user);
        return entityToBean(user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public UserResponseBean update(@PathVariable("id") Integer id,
                                     @RequestBody UserRequestBean bean){
        bean.setId(id);
        User user = beanToEntity(bean);
        user = service.update(user);
        return entityToBean(user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Integer id){
        service.delete(id);
    }


    private User beanToEntity(UserRequestBean bean) {
        User entity = new User();
        entity.setId(bean.getId());
        entity.setFirstName(bean.getFirstName());
        entity.setMiddleName(bean.getMiddleName());
        entity.setLastName(bean.getLastName());
        entity.setLogin(bean.getLogin());
        entity.setPassword(bean.getPassword());
        entity.setBirthday(bean.getBirthday());
        return entity;
    }

    private UserResponseBean entityToBean(User entity) {
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
}
