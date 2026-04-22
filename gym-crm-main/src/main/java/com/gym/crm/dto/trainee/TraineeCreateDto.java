package com.gym.crm.dto.trainee;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeCreateDto {
    @NotBlank(message="Trainees first name is required.")
    private String firstname;
    @NotBlank(message="Trainees last name is required.")
    private String lastname;

    private LocalDate dateOfBirth;
    private String address;
}