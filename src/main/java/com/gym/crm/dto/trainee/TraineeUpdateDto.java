package com.gym.crm.dto.trainee;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeUpdateDto {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    @Past(message="Date of birth must be in the past.")
    private LocalDate dateOfBirth;
    private String address;
    private boolean isActive;
}