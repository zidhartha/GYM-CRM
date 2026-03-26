package com.gym.crm.health;

import com.gym.crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserHealthIndicator implements HealthIndicator {

    private final UserService userService;

    @Override
    public Health health() {
        try {
            long count = userService.countUsers();
            if (count == 0) {
                return Health.unknown()
                        .withDetail("warning", "No users registered in the system")
                        .withDetail("userCount", 0)
                        .build();
            }
            return Health.up()
                    .withDetail("userCount", count)
                    .withDetail("status", "User data is accessible")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", "Cannot access user data")
                    .withDetail("reason", e.getMessage())
                    .build();
        }
    }
}