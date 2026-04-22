package com.gym.crm.dto.trainingType;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TrainingTypeListDto {
    private List<TrainingTypeProfileDto> trainingTypes;
}
