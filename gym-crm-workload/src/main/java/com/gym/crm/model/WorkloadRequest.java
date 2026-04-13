package com.gym.crm.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkloadRequest {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private boolean active;
    private LocalDate trainingDate;
    private double trainingDuration;
    private ActionType actionType;
}
