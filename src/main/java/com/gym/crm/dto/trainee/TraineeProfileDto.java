package com.gym.crm.dto.trainee;

import com.gym.crm.dto.trainer.TrainerListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraineeProfileDto {
    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String address;

    private boolean isActive;

    private TrainerListDto trainers;
}
