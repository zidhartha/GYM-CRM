package com.gym.crm.metrics;

import com.gym.crm.service.TraineeService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActiveTraineeMetrics {
    private final MeterRegistry registry;
    private final TraineeService traineeService;

    @PostConstruct
    public void init() {
        registry.gauge("gym.total_trainees", traineeService, TraineeService::countTrainees);
    }
}
