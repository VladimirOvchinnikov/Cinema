package com.test.cinema.model.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "v_users")
public class User extends BaseEntity{

    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate birthday;
    private String login;
    private String password;
    private Boolean isAuthorization;

    @Column(name = "first_name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "middle_name")
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Column(name = "last_name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "birthday")
    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    @Column(name = "login")
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Transient
    public Boolean getIsAuthorization() {
        return isAuthorization;
    }

    public void setIsAuthorization(Boolean isAuthorization) {
        this.isAuthorization = isAuthorization;
    }
}
