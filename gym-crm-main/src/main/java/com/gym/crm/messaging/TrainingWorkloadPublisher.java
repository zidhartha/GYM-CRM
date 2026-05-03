package com.gym.crm.messaging;


import com.gym.crm.dto.trainer.TrainerWorkloadRequestDto;
import com.gym.crm.model.ActionType;
import com.gym.crm.model.Training;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingWorkloadPublisher {

    private final JmsTemplate jmsTemplate;

    @Value("${activemq.queue.workload}")
    private String workloadQueue;

    public void notifyWorkload(Training training, ActionType actionType, String transactionId) {
        TrainerWorkloadRequestDto request = toRequest(training, actionType);
        try {
            jmsTemplate.convertAndSend(workloadQueue, request, message -> {
                message.setStringProperty("transactionId", transactionId);
                return message;
            });
            log.info("[txId={}] Sent workload message for trainer '{}', action={}",
                    transactionId, request.getTrainerUsername(), actionType);
        } catch (Exception e) {
            log.error("[txId={}] Failed to send workload message: {}", transactionId, e.getMessage());
        }
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