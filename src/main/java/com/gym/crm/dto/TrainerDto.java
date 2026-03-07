package com.gym.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerDto {

    @NotBlank(message="Trainers first name is required.")
    private String firstname;

    @NotBlank(message="Trainers last name is required.")
    private String lastname;

    @NotBlank(message="Trainers specialization is required.")
    private String specialization;
}