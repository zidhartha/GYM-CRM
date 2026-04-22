package com.gym.crm.client;

import com.gym.crm.dto.trainer.TrainerWorkloadRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadClient {

    private final WebClient.Builder webClientBuilder;

    @Value(("${workload.url}"))
    private String workloadUrl;

    public void sendWorkload(TrainerWorkloadRequestDto request, String transactionId) {
        String txId = transactionId != null ? transactionId : UUID.randomUUID().toString();

        webClientBuilder.build()
                .post()
                .uri(workloadUrl)
                .header("X-Transaction-Id", txId)
                // No .header("Authorization", ...) needed — the OAuth2 filter handles it
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(5))
                .block();
    }
}