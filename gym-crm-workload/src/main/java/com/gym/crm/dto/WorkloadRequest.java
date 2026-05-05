package com.gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull(message = "trainingDate must not be null")
    private LocalDate trainingDate;

    @Positive(message = "trainingDuration must be positive")
    private int trainingDuration;

    @NotNull(message = "actionType must not be null")
    private ActionType actionType;
}
