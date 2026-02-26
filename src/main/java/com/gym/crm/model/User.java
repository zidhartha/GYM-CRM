package com.gym.crm.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@EqualsAndHashCode
public class User {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive = true;


    public User(Long id, String firstName, String lastName, String username, String password, boolean isActive) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.isActive = isActive;
    }


    public User(){

    }


}
