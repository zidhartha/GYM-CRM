package com.gym.crm.metrics;

import com.gym.crm.service.TrainerService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActiveTrainerMetrics {
    private final MeterRegistry registry;
    private final TrainerService trainerService;

    @PostConstruct
    public void init() {
        registry.gauge("gym.total_trainers", trainerService, TrainerService::countTrainers);
    }
}
