package com.gym.crm.Loader;


import com.gym.crm.dto.TrainingDto;

import com.gym.crm.service.TrainingService;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
@RequiredArgsConstructor
public class TrainingLoader implements Loader {

    private final TrainingService trainingService;
    private List<TrainingDto> trainings;

    public void setTrainings(List<TrainingDto> trainings) {
        this.trainings = trainings;
    }

    @Override
    public int getOrder() {
        return 5;
    }

    @Override

    public void load() {
        if (trainings == null) return;
        for (TrainingDto dto : trainings) {
            trainingService.createTraining(dto);
        }
    }
}