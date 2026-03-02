package com.gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeDto {

    @NotBlank(message="Trainees first name is required.")
    private String firstname;

    @NotBlank(message="Trainees last name is required.")
    private String lastname;



    private LocalDate dateOfBirth;

    @NotBlank(message="Trainees address is required.")
    private String address;
}