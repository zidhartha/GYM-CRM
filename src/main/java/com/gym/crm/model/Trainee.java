package com.gym.crm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode()
@ToString
@Entity
@Table(name="trainee")
@AllArgsConstructor
@NoArgsConstructor

public class Trainee{

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="trainee_id")
    private Long id;

    @Column(name="date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name="address")
    private String address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user_id",nullable=false,unique=true)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "trainee_trainer",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id")
    )
    private List<Trainer> trainers = new ArrayList<>();

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Training> trainings = new ArrayList<>();

    public Trainee(User user, LocalDate dateOfBirth, String address){
        this.user = user;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

}
