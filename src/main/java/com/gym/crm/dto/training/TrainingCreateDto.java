package com.gym.crm.dto.training;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingCreateDto {
    @NotBlank(message="trainee username is required.")
    private String traineeUsername;

    @NotBlank(message="trainer username is required.")
    private String trainerUsername;

    @NotBlank(message="Training name is required.")
    private String trainingName;

    @NotBlank(message="Training type name is required.")
    private String trainingTypeName;

    @NotNull(message ="Training date must not be null.")
    private LocalDate trainingDate;

    @Positive(message="Training duration must be a positive number")
    private Long trainingDuration;
}