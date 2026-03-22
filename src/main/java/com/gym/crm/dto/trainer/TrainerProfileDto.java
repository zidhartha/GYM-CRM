package com.gym.crm.dto.trainer;

import com.gym.crm.dto.trainee.TraineeListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerProfileDto {
    private String firstName;
    private String lastName;
    private String username;
    private String specialization;
    private boolean isActive;

    private TraineeListDto trainees;
}
