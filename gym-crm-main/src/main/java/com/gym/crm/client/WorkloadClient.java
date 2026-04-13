package com.gym.crm.client;

import com.gym.crm.dto.trainer.TrainerWorkloadRequestDto;
import com.gym.crm.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadClient {
    private final WebClient.Builder webClientBuilder;
    private final JwtService jwtService;

    private static final String WORKLOAD_URL = "http://workload-service/api/workload";

    public void sendWorkload(TrainerWorkloadRequestDto request, String transactionId) {
        String token = jwtService.generateTokenForService();
        String txId = transactionId != null ? transactionId : UUID.randomUUID().toString();

        webClientBuilder.build()
                .post()
                .uri(WORKLOAD_URL)
                .header("Authorization", "Bearer " + token)
                .header("X-Transaction-Id", txId)
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}