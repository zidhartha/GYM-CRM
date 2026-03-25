package com.gym.crm.metrics;

import com.gym.crm.service.UserService;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActiveUserMetrics {
    private final MeterRegistry registry;
    private final UserService userService;

    @PostConstruct
    public void init() {
        registry.gauge("gym.active_users", userService, UserService::countUsers);
    }
}
