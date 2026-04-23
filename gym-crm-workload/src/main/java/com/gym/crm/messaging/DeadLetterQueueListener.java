package com.gym.crm.messaging;

import jakarta.jms.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeadLetterQueueListener {

    @JmsListener(destination = "ActiveMQ.DLQ", containerFactory = "jmsListenerContainerFactory")
    public void onDeadLetter(Message message) {
        try {
            String txId = message.getStringProperty("transactionId");
            String cause = message.getStringProperty("dlqDeliveryFailureCause");
            log.error("[txId={}] Dead letter received — messageId='{}', cause='{}'",
                    txId, message.getJMSMessageID(), cause);
        } catch (Exception e) {
            log.error("Failed to inspect dead letter message", e);
        }
    }
}