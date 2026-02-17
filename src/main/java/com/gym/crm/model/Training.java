package com.gym.crm.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString
public class Training {
    private Long id;
    private Long trainerId;
    private Long traineeId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private Integer trainingDuration;

    public Training(Long trainingId, Long trainerId, Long traineeId, String trainingName, TrainingType trainingType, LocalDate trainingDate, Integer trainingDuration) {
        this.id = trainingId;
        this.trainerId = trainerId;
        this.traineeId = traineeId;
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    public Training(){

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Training training = (Training) o;
        return Objects.equals(id, training.id) &&
                Objects.equals(traineeId, training.traineeId) &&
                Objects.equals(trainerId, training.trainerId) &&
                Objects.equals(trainingName, training.trainingName) &&
                Objects.equals(trainingType, training.trainingType) &&
                Objects.equals(trainingDate, training.trainingDate) &&
                Objects.equals(trainingDuration, training.trainingDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, trainerId, traineeId, trainingType,trainingName, trainingDate, trainingDuration);
    }
}
