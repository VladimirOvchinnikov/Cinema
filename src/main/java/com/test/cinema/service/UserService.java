package com.test.cinema.service;

import com.test.cinema.exception.RestException;
import com.test.cinema.model.entity.User;
import com.test.cinema.repository.UserRepository;
import com.test.cinema.service.util.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private MessageService messageService;


    public Page<User> getList(Map<String, Object> filter, Integer page, Integer perPage) {
        Page<User> users;
        if (filter.get("id") != null) {
            User t = new User();
            t.setId((Integer) filter.get("id"));
            users = repository.findAll(Example.of(t), PageRequest.of(page, perPage));
        } else {
            users = repository.findAll(PageRequest.of(page, perPage, new Sort(Sort.Direction.ASC, "createdAt")));
        }
        return users;
    }

    public User getById(Integer id) {
        Map<String, Object> filter = new HashMap<>();
        filter.putIfAbsent("id", id);
        List<User> seances = getList(filter, 0, 1).getContent();
        return CollectionUtils.isEmpty(seances) ? null : seances.get(0);
    }

    public User create(User user) {
        validate(user);
        user = repository.saveAndFlush(user);
        return user;
    }

    public User update(User user) {
        validate(user);
        User instance = getById(user.getId());
        if (instance == null) {
            throw new RestException(messageService.getMessage("user.error.not-found", user.getId()));
        }
        user.setCreatedAt(instance.getCreatedAt());
        user = repository.saveAndFlush(user);
        return user;
    }

    @Transactional
    public void delete(Integer id) {
        repository.delete(id);
    }

    private void validate(User user) {
        if (StringUtils.isEmpty(user.getFirstName())) {
            throw new RestException(messageService.getMessage("user.error.not-found.first-name"));
        }
        if (StringUtils.isEmpty(user.getMiddleName())) {
            throw new RestException(messageService.getMessage("user.error.not-found.middle-name"));
        }
        if (StringUtils.isEmpty(user.getLastName())) {
            throw new RestException(messageService.getMessage("user.error.not-found.last-name"));
        }
        if (user.getBirthday() == null) {
            throw new RestException(messageService.getMessage("user.error.not-found.birthday"));
        }
        if (StringUtils.isEmpty(user.getLogin())) {
            throw new RestException(messageService.getMessage("user.error.not-found.login"));
        }
        if (StringUtils.isEmpty(user.getPassword())) {
            throw new RestException(messageService.getMessage("user.error.not-found.password"));
        }
    }
}
