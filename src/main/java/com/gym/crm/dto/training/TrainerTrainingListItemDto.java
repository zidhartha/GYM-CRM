package com.gym.crm.dto.training;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerTrainingListItemDto {
    private String trainingName;

    private LocalDate trainingDate;

    private Long trainingTypeId;

    private Long trainingDuration;

    private String traineeUsername;
}
