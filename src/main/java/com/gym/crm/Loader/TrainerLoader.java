package com.gym.crm.Loader;

import com.gym.crm.dto.TrainerDto;
import com.gym.crm.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
@RequiredArgsConstructor
public class TrainerLoader implements Loader {

    private final TrainerService trainerService;
    private List<TrainerDto> trainers;

    public void setTrainers(List<TrainerDto> trainers) {
        this.trainers = trainers;
    }

    @Override
    public int getOrder() {
        return 4;
    }

    @Override

    public void load() {
        if (trainers == null) return;
        for (TrainerDto dto : trainers) {
            trainerService.createTrainer(dto);
        }
    }
}