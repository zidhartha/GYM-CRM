package component.steps;

import com.gym.crm.model.TrainerWorkload;
import com.gym.crm.repository.TrainerWorkloadRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class WorkloadComponentSteps {

    @Autowired private MockMvc mockMvc;
    @Autowired private TrainerWorkloadRepository repository;

    private MvcResult lastResult;

    @Before
    public void cleanup() {
        repository.deleteAll();
    }

    @When("a workload update is submitted for trainer {string} with action {string} and duration {int} on {string}")
    public void submitWorkload(String username, String action, int duration, String date) throws Exception {
        String body = """
            {
              "trainerUsername": "%s",
              "trainerFirstName": "Test",
              "trainerLastName": "Trainer",
              "active": true,
              "trainingDate": "%s",
              "trainingDuration": %d,
              "actionType": "%s"
            }
            """.formatted(username, date, duration, action);

        lastResult = mockMvc.perform(post("/api/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
    }

    @When("a workload update is submitted with missing trainer username")
    public void submitInvalidWorkload() throws Exception {
        String body = """
            {
              "trainerUsername": "",
              "trainerFirstName": "Test",
              "trainerLastName": "Trainer",
              "active": true,
              "trainingDate": "2025-01-01",
              "trainingDuration": 60,
              "actionType": "ADD"
            }
            """;

        lastResult = mockMvc.perform(post("/api/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
    }

    @When("the workload summary is requested for trainer {string}")
    public void getWorkload(String username) throws Exception {
        lastResult = mockMvc.perform(get("/api/workload/" + username))
                .andReturn();
    }

    @Then("the workload response status should be {int}")
    public void checkStatus(int status) {
        assertEquals(status, lastResult.getResponse().getStatus());
    }

    @And("the workload for trainer {string} should show {int} minutes in month {int} of year {int}")
    public void verifyDuration(String username, int expectedMinutes, int month, int year) {
        int actual = waitForWorkloadMinutes(username, month, year);
        assertEquals(expectedMinutes, actual);
    }

    /** Polls Mongo until the month entry exists (covers async JMS; fast path for HTTP tests). */
    private int waitForWorkloadMinutes(String username, int month, int year) {
        final long timeoutMs = 15000;
        final long pollMs = 100;
        final long deadline = System.currentTimeMillis() + timeoutMs;

        while (System.currentTimeMillis() < deadline) {
            var workloadOpt = repository.findByUsername(username);
            if (workloadOpt.isPresent()) {
                var durationOpt = workloadOpt.get().getYearlySummary().stream()
                        .filter(y -> y.getYear() == year)
                        .flatMap(y -> y.getMonths().stream())
                        .filter(m -> m.getMonth() == month)
                        .map(m -> m.getTotalDurationMinutes())
                        .findFirst();
                if (durationOpt.isPresent()) {
                    return durationOpt.get();
                }
            }
            try {
                Thread.sleep(pollMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for workload duration", e);
            }
        }

        throw new AssertionError("No workload for " + username + " with data for " + year + "-" + month
                + ". Documents: " + repository.findAll());
    }
}
