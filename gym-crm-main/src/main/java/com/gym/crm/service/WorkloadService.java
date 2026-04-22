package com.gym.crm.service;

import com.gym.crm.client.WorkloadClient;
import com.gym.crm.dto.trainer.TrainerWorkloadRequestDto;
import com.gym.crm.model.ActionType;
import com.gym.crm.model.Training;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadService {

    private final WorkloadClient workloadClient;

    @CircuitBreaker(name = "workload", fallbackMethod = "handleWorkloadFailure")
    @Retry(name = "workload")
    public void notifyWorkload(Training training, ActionType actionType, String transactionId) {
        workloadClient.sendWorkload(toRequest(training, actionType), transactionId);
    }

    public void handleWorkloadFailure(Training training, ActionType actionType,
                                      String transactionId, Throwable t) {
        log.warn("[txId={}] Workload unavailable — {} training '{}'. Reason: {}",
                transactionId, actionType, training.getTrainingName(), t.getMessage());
    }

    private TrainerWorkloadRequestDto toRequest(Training training, ActionType actionType) {
        return TrainerWorkloadRequestDto.builder()
                .trainerUsername(training.getTrainer().getUser().getUsername())
                .trainerFirstName(training.getTrainer().getUser().getFirstName())
                .trainerLastName(training.getTrainer().getUser().getLastName())
                .active(training.getTrainer().getUser().isActive())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .actionType(actionType)
                .build();
    }
}
