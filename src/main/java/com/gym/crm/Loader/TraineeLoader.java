package com.gym.crm.Loader;

import com.gym.crm.dto.TraineeDto;
import com.gym.crm.service.TraineeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.List;


@Component
@RequiredArgsConstructor
public class TraineeLoader implements Loader {

    private final TraineeService traineeService;
    private List<TraineeDto> trainees;

    public void setTrainees(List<TraineeDto> trainees) {
        this.trainees = trainees;
    }

    @Override
    public int getOrder() {
        return 3;
    }

    @Override

    public void load() {

        if (trainees == null) return;
        for (TraineeDto dto : trainees) {
            traineeService.createTrainee(dto);
        }
    }
}