package com.gym.crm.dto;


import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeUpdateDto {
    private String firstName;
    private String lastName;
    private String password;

    @Past(message="Date of birth must be in the past.")
    private LocalDate dateOfBirth;
    private String address;

}