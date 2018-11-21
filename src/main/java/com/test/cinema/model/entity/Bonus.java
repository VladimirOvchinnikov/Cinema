package com.test.cinema.model.entity;

public class Bonus {

    private Long id;
    private String name;
    private String description;
    private TypeBonus typeBonus;
    private TypeUser typeUser;
//    private String instruction; // продумать момент с акцией из бд
    // по факту сейчас только 2 типа акции это скидка и халявный билет

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeBonus getTypeBonus() {
        return typeBonus;
    }

    public void setTypeBonus(TypeBonus typeBonus) {
        this.typeBonus = typeBonus;
    }

    public TypeUser getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(TypeUser typeUser) {
        this.typeUser = typeUser;
    }
}
