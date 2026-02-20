package com.gym.crm.Loader;

import com.gym.crm.service.TraineeService;
import com.gym.crm.storage.StorageInitializer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TraineeLoader {

    private final TraineeService traineeService;

    public TraineeLoader(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    public void load(List<StorageInitializer.TraineeSeed> trainees) {
        if (trainees == null) return;

        trainees.forEach(t ->
                traineeService.createTrainee(
                        t.getFirstName(),
                        t.getLastName(),
                        t.getDateOfBirth(),
                        t.getAddress()
                )
        );
    }
}

