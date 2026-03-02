package com.gym.crm.model;


import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name="users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Column(name="fname",nullable=false,length=55)
    private String firstName;

    @Column(name="lname",nullable=false,length=55)
    private String lastName;

    @Column(name="username",nullable=false,length=55)
    private String username;

    @Column(name="password",nullable=false)
    private String password;

    @Column(name="active",nullable=false)
    private boolean active;

    public User(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.active = true;
    }




}
