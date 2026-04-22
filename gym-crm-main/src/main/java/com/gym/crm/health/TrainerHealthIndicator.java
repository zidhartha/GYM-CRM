package com.gym.crm.health;

import com.gym.crm.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainerHealthIndicator implements HealthIndicator {

    private final TrainerService trainerService;

    @Override
    public Health health() {
        try {
            long count = trainerService.countTrainers();
            if (count == 0) {
                return Health.unknown()
                        .withDetail("warning", "No trainers registered in the system")
                        .withDetail("trainerCount", 0)
                        .build();
            }
            return Health.up()
                    .withDetail("trainerCount", count)
                    .withDetail("status", "Trainer data is accessible")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", "Cannot access trainer data")
                    .withDetail("reason", e.getMessage())
                    .build();
        }
    }
}
