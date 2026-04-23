package com.gym.crm.Handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Slf4j
@Component
public class JmsErrorHandler implements ErrorHandler {
    @Override
    public void handleError(Throwable t) {
        log.error("JMS listener error.Message will be redelivered or sent to DLQ. Reason: {}",
                t.getMessage(), t);
    }
}