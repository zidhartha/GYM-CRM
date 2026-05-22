package com.gym.crm.integration.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.SharedState;
import com.gym.crm.service.JwtService;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TrainingIntegrationSteps {

    @Autowired private JmsTemplate jmsTemplate;

    @Before
    public void resetJmsSpy() {
        clearInvocations(jmsTemplate);
    }

    @And("a workload message should have been sent to the queue")
    public void verifyMessageSent() {
        verify(jmsTemplate).convertAndSend(eq("gym.workload.queue"), any(), any());
    }

    @And("no workload message should have been sent to the queue")
    public void verifyNoMessageSent() {
        verify(jmsTemplate, never()).convertAndSend(eq("gym.workload.queue"), any(), any());
    }
}