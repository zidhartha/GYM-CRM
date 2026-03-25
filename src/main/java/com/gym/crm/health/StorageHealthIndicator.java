package com.gym.crm.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import java.io.File;


@Component("storage")
public class StorageHealthIndicator implements HealthIndicator {

    private final File path;
    private final long minFreeMb;

    public StorageHealthIndicator(
            @Value("${health.uploads.path:./uploads}") String path,
            @Value("${health.uploads.min-free-mb:100}") long minFreeMb) {
        this.path = new File(path);
        this.minFreeMb = minFreeMb;
    }

    @Override
    public Health health() {
        if (!path.exists()) {
            return Health.down()
                    .withDetail("reason", "uploads directory does not exist!?")
                    .withDetail("path", path.getAbsolutePath())
                    .build();
        }
        long freeMb = path.getUsableSpace() / (1024 * 1024);
        if (freeMb >= minFreeMb) {
            return Health.up()
                    .withDetail("path", path.getAbsolutePath())
                    .withDetail("freeMb", freeMb)
                    .withDetail("minFreeMb", minFreeMb)
                    .build();
        } else {
            return Health.outOfService()
                    .withDetail("path", path.getAbsolutePath())
                    .withDetail("freeMb", freeMb)
                    .withDetail("minFreeMb", minFreeMb)
                    .withDetail("reason", "Disk space below minimum threshold")
                    .build();
        }
    }
}