package com.test.cinema.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.test.cinema.util.Constant.PATTERN_DATE;
import static com.test.cinema.util.Constant.PATTERN_DATE_TIME;

public class UserResponseBean implements ResponseBean {

    private Integer id;
    @JsonFormat(pattern = PATTERN_DATE_TIME)
    private LocalDateTime createdAt;
    private String firstName;
    private String middleName;
    private String lastName;
    @JsonFormat(pattern = PATTERN_DATE)
    private LocalDate birthday;
    private String login;
    private String password;
    private Boolean isAuthorization;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsAuthorization() {
        return isAuthorization;
    }

    public void setIsAuthorization(Boolean isAuthorization) {
        this.isAuthorization = isAuthorization;
    }
}
