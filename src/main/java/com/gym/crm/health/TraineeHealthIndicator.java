package com.gym.crm.health;

import com.gym.crm.service.TraineeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TraineeHealthIndicator implements HealthIndicator {
    private final TraineeService traineeService;

    @Override
    public Health health() {
        try{
            long count = traineeService.countTrainees();
            if (count == 0) {
                return Health.unknown()
                        .withDetail("warning","No trainees registered in the system")
                        .withDetail("traineeCount",0)
                        .build();
            }
            return Health.up()
                    .withDetail("traineeCount",count)
                    .withDetail("status","Trainee data is accessible")
                    .build();
        }catch(Exception e){
            return Health.down()
                    .withDetail("error","Cannot access trainee data")
                    .withDetail("reason",e.getMessage())
                    .build();
        }
    }
}
