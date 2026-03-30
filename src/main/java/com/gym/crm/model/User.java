package com.gym.crm.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name="\"user\"")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Column(name="f_name",nullable=false,length=55)
    private String firstName;

    @Column(name="l_name",nullable=false,length=55)
    private String lastName;

    @Column(name="username",nullable=false,length=55,unique = true)
    private String username;

    @Column(name="password",nullable=false)
    private String password;

    @Column(name="active",nullable=false)
    private boolean active;

    @Column(name = "failed_attempts")
    private int failedAttempts = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "last_logout")
    private LocalDateTime lastLogout;

    public User(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.active = true;
    }

}
