package com.gym.crm.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerUpdateDto {
    @NotBlank(message="Trainers specialization is required.")
    private String specialization;
}