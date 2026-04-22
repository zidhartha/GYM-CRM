package com.gym.crm.dto.trainer;

import com.gym.crm.model.ActionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
@Data
@Builder
public class TrainerWorkloadRequestDto {
    private String trainerUsername;
    private String trainerFirstName;
    private String trainerLastName;
    private boolean active;
    private LocalDate trainingDate;
    private double trainingDuration;
    private ActionType actionType;
}
