package integration.steps;

import com.gym.crm.dto.WorkloadRequest;
import com.gym.crm.dto.ActionType;
import com.gym.crm.model.TrainerWorkload;
import com.gym.crm.repository.TrainerWorkloadRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class WorkloadIntegrationSteps {
    private static final String WORKLOAD_QUEUE = "gym.workload.queue";
    private static final String DLQ_QUEUE = "DLQ.gym.workload.queue";

    @Autowired private JmsTemplate jmsTemplate;
    @Autowired private TrainerWorkloadRepository repository;

    @Before
    public void drainQueue() {
        // Drain any leftover messages from previous scenarios
        jmsTemplate.setReceiveTimeout(500);
        while (jmsTemplate.receive("gym.workload.queue") != null) {}
        while (jmsTemplate.receive("DLQ.gym.workload.queue") != null) {}
    }

    @Before
    public void resetState() {
        repository.deleteAll();
    }

    @When("a JMS workload message is sent for trainer {string} with action {string} and duration {int} on {string}")
    public void sendJmsMessage(String username, String action, int duration, String date) {
        WorkloadRequest request = new WorkloadRequest();
        request.setTrainerUsername(username);
        request.setTrainerFirstName("JMS");
        request.setTrainerLastName("Test");
        request.setActive(true);
        request.setTrainingDate(LocalDate.parse(date));
        request.setTrainingDuration(duration);
        request.setActionType(ActionType.valueOf(action));

        jmsTemplate.convertAndSend(WORKLOAD_QUEUE, request);
    }

    @When("an invalid JMS workload message is sent with blank trainer username")
    public void sendInvalidJmsMessage() {
        WorkloadRequest request = new WorkloadRequest();
        request.setTrainerUsername("");
        request.setTrainerFirstName("Bad");
        request.setTrainerLastName("Message");
        request.setActive(true);
        request.setTrainingDate(LocalDate.of(2025, 1, 1));
        request.setTrainingDuration(60);
        request.setActionType(ActionType.ADD);

        jmsTemplate.convertAndSend(WORKLOAD_QUEUE, request);
    }

    @And("the message is processed")
    public void waitForProcessing() throws InterruptedException {
        Thread.sleep(2000);
    }

    @Then("no workload record should exist for trainer {string}")
    public void verifyNoRecord(String username) {
        Optional<TrainerWorkload> workload = repository.findByUsername(username);
        assertTrue(workload.isEmpty(), "Expected no workload record but found one");
    }

}