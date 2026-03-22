package com.gym.crm.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString()
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="training")
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="training_id")
    @ToString.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainee_id", nullable = false)
    @ToString.Include
    private Trainee trainee;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    @ToString.Include
    private Trainer trainer;

    @Column(name="training_name",nullable = false)
    @ToString.Include
    private String trainingName;

    @ManyToOne
    @JoinColumn(name="training_type_id",nullable=false)
    @ToString.Include
    private TrainingType trainingType;

    @Column(name="training_date",nullable=false)
    @ToString.Include
    private LocalDate trainingDate;

    @Column(name="training_duration",nullable=false)
    @ToString.Include
    private Long trainingDuration;

    public Training(Trainee trainee, Trainer trainer, TrainingType trainingType, String trainingName, LocalDate trainingDate, Long trainingDuration) {
        this.trainee = trainee;
        this.trainer = trainer;
        this.trainingType = trainingType;
        this.trainingName = trainingName;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }
}
