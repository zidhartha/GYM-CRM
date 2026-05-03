package com.gym.crm.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkloadRequest {
    @NotBlank(message = "trainerUsername must not be blank")
    private String trainerUsername;
    @NotBlank(message = "trainerFirstName must not be blank")
    private String trainerFirstName;
    @NotBlank(message = "trainerLastName must not be blank")
    private String trainerLastName;
    private boolean active;
    private LocalDate trainingDate;
    @NotNull(message="trainingDuration must not be null")
    private double trainingDuration;
    @NotNull(message = "actionType must not be null")
    private ActionType actionType;
}
