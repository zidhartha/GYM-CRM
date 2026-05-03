package com.gym.crm.messaging;

import com.gym.crm.model.WorkloadRequest;
import com.gym.crm.service.WorkloadService;
import jakarta.jms.Message;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkloadListener {

    private final WorkloadService workloadService;
    private final Validator validator;


    @JmsListener(destination = "${activemq.queue.workload}", containerFactory = "jmsListenerContainerFactory")
    public void onMessage(WorkloadRequest request, Message rawMessage) throws Exception {
        String txId = Optional.ofNullable(rawMessage.getStringProperty("transactionId")).orElse("no-tx");
        MDC.put("transactionId", txId);

        try {
            Set<ConstraintViolation<WorkloadRequest>> violations = validator.validate(request);
            if (!violations.isEmpty()) {
                String errors = violations.stream()
                        .map(v -> v.getPropertyPath() + " " + v.getMessage())
                        .collect(Collectors.joining(", "));
                log.error("[txId={}] Invalid message — will be redelivered then dead-lettered. Violations: {}", txId, errors);
                throw new IllegalArgumentException("Validation failed: " + errors);
            }

            workloadService.updateWorkload(request);
            log.info("[txId={}] Workload updated for trainer: {}", txId, request.getTrainerUsername());
        } finally {
            MDC.clear();
        }
    }

    private boolean isValid(WorkloadRequest request) {
        Set<ConstraintViolation<WorkloadRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .collect(Collectors.joining(", "));

            log.error("Invalid message routed to DLQ.Violations: {}", errors);
            return false;
        }
        return true;
    }
}