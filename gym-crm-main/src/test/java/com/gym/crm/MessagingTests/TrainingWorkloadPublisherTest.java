package com.gym.crm.MessagingTests;

import com.gym.crm.messaging.TrainingWorkloadPublisher;
import com.gym.crm.model.ActionType;
import com.gym.crm.model.Training;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.User;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingWorkloadPublisherTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private TrainingWorkloadPublisher publisher;

    private Training training;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(publisher, "workloadQueue", "gym.workload.queue");

        User user = new User();
        user.setUsername("nika.cire");
        user.setFirstName("Nika");
        user.setLastName("Cire");
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        training = new Training();
        training.setTrainer(trainer);
        training.setTrainingDate(LocalDate.of(2025, 4, 1));
        training.setTrainingDuration(2L);
    }


    @Test
    void notifyWorkload_validInput_callsJmsTemplate() {
        publisher.notifyWorkload(training, ActionType.ADD, "tx-123");

        verify(jmsTemplate, times(1))
                .convertAndSend(eq("gym.workload.queue"), any(), any(MessagePostProcessor.class));
    }

    @Test
    void notifyWorkload_validInput_sendsToCorrectQueue() {
        ArgumentCaptor<String> queueCaptor = ArgumentCaptor.forClass(String.class);

        publisher.notifyWorkload(training, ActionType.ADD, "tx-123");

        verify(jmsTemplate).convertAndSend(
                queueCaptor.capture(), any(), any(MessagePostProcessor.class));
        assertEquals("gym.workload.queue", queueCaptor.getValue());
    }

    @Test
    void notifyWorkload_addAction_doesNotThrow() {
        assertDoesNotThrow(() ->
                publisher.notifyWorkload(training, ActionType.ADD, "tx-123"));
    }

    @Test
    void notifyWorkload_deleteAction_doesNotThrow() {
        assertDoesNotThrow(() ->
                publisher.notifyWorkload(training, ActionType.DELETE, "tx-456"));
    }


    @Test
    void notifyWorkload_mapsTrainerUsernameCorrectly() {
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);

        publisher.notifyWorkload(training, ActionType.ADD, "tx-123");
        verify(jmsTemplate).convertAndSend(
                any(String.class), payloadCaptor.capture(), any(MessagePostProcessor.class));

        var dto = (com.gym.crm.dto.trainer.TrainerWorkloadRequestDto) payloadCaptor.getValue();
        assertEquals("nika.cire", dto.getTrainerUsername());
    }

    @Test
    void notifyWorkload_mapsActionTypeCorrectly() {
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);

        publisher.notifyWorkload(training, ActionType.DELETE, "tx-123");

        verify(jmsTemplate).convertAndSend(
                any(String.class), payloadCaptor.capture(), any(MessagePostProcessor.class));

        var dto = (com.gym.crm.dto.trainer.TrainerWorkloadRequestDto) payloadCaptor.getValue();
        assertEquals(ActionType.DELETE, dto.getActionType());
    }

    @Test
    void notifyWorkload_mapsTrainingDateCorrectly() {
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);

        publisher.notifyWorkload(training, ActionType.ADD, "tx-123");

        verify(jmsTemplate).convertAndSend(
                any(String.class), payloadCaptor.capture(), any(MessagePostProcessor.class));

        var dto = (com.gym.crm.dto.trainer.TrainerWorkloadRequestDto) payloadCaptor.getValue();
        assertEquals(LocalDate.of(2025, 4, 1), dto.getTrainingDate());
    }


    @Test
    void notifyWorkload_jmsTemplateThrows_doesNotPropagate() {
        doThrow(new RuntimeException("broker down"))
                .when(jmsTemplate)
                .convertAndSend(any(String.class), any(Object.class), any(MessagePostProcessor.class));

        assertDoesNotThrow(() ->
                publisher.notifyWorkload(training, ActionType.ADD, "tx-123"));
    }

    @Test
    void notifyWorkload_jmsTemplateThrows_stillLogsError() {
        doThrow(new RuntimeException("broker down"))
                .when(jmsTemplate)
                .convertAndSend(any(String.class), any(Object.class), any(MessagePostProcessor.class));

        publisher.notifyWorkload(training, ActionType.ADD, "tx-123");

        verify(jmsTemplate, times(1))
                .convertAndSend(any(String.class), any(Object.class), any(MessagePostProcessor.class));
    }

    @Test
    void notifyWorkload_setsTransactionIdHeader() throws JMSException {
        Message mockMessage = mock(Message.class);
        // removed the when() line — setStringProperty is void, Mockito mocks it automatically

        ArgumentCaptor<MessagePostProcessor> processorCaptor =
                ArgumentCaptor.forClass(MessagePostProcessor.class);

        publisher.notifyWorkload(training, ActionType.ADD, "tx-999");

        verify(jmsTemplate).convertAndSend(
                any(String.class), any(), processorCaptor.capture());

        processorCaptor.getValue().postProcessMessage(mockMessage);

        verify(mockMessage).setStringProperty("transactionId", "tx-999");
    }
}