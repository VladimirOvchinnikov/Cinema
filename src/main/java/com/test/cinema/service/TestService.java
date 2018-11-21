package com.test.cinema.service;

import com.test.cinema.model.entity.User;
import com.test.cinema.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {

    @Autowired
    private TestRepository repository;

    public List<User> getList(){
        return repository.findAll();
    }
}
