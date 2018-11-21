package com.test.cinema.controller;

import com.test.cinema.model.entity.User;
import com.test.cinema.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TestController {

    @Autowired
    private TestService service;


    @RequestMapping(method = RequestMethod.GET)
    public String get() {
        System.out.println("ssss");
        return "AAAAA";
    }


    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public List<User> getList() {
        System.out.println("aaaaaaaaaaaa");
        return service.getList();

    }


    public boolean getRandom() {
        return Math.random() < 0.1;
    }

    public static void main(String[] args) {
        System.out.println(100 % 10==0);
        System.out.println(0 % 10==0);

        System.out.println(102 % 10==0);
        System.out.println(109 % 10==0);
        System.out.println(9 % 10==0);
    }


}
