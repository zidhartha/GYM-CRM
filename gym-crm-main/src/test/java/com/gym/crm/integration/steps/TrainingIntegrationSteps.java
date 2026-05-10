package com.gym.crm.integration.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.integration.IntegrationSharedState;
import com.gym.crm.service.JwtService;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
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

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private IntegrationSharedState state;
    @Autowired private JmsTemplate jmsTemplate;


    @Before
    public void resetJmsSpy() {
        clearInvocations(jmsTemplate);
    }

    @Given("a registered trainee with first name {string} and last name {string}")
    public void registerTrainee(String firstName, String lastName) throws Exception {
        String body = """
            {"firstname":"%s","lastname":"%s","dateOfBirth":"2000-01-01","address":"Test St"}
            """.formatted(firstName, lastName);

        state.lastResult = mockMvc.perform(post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();

        assertEquals(201, state.lastResult.getResponse().getStatus());

        JsonNode json = objectMapper.readTree(state.lastResult.getResponse().getContentAsString());
        state.traineeUsername = json.get("username").asText();
        state.lastRegisteredUsername = state.traineeUsername;
    }

    @Given("a registered trainer with first name {string} and last name {string} specializing in {string}")
    public void registerTrainer(String firstName, String lastName, String specialization) throws Exception {
        String body = """
            {"firstname":"%s","lastname":"%s","specialization":"%s"}
            """.formatted(firstName, lastName, specialization);

        var result = mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();

        assertEquals(201, result.getResponse().getStatus());

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        state.trainerUsername = json.get("username").asText();

        if (state.lastRegisteredUsername == null) {
            state.lastRegisteredUsername = state.trainerUsername;
        }
    }

    @Given("the user is authenticated")
    public void authenticate() {
        state.jwtToken = jwtService.generateToken(state.lastRegisteredUsername);
    }

    @Given("the user is authenticated as any user")
    public void authenticateAsAny() {
        if (state.lastRegisteredUsername != null) {
            state.jwtToken = jwtService.generateToken(state.lastRegisteredUsername);
        } else {
            state.jwtToken = jwtService.generateToken("system.user");
        }
    }

    @When("a training is created with name {string} on {string} for {int} minutes")
    public void createTraining(String name, String date, int duration) throws Exception {
        String body = """
            {
              "traineeUsername": "%s",
              "trainerUsername": "%s",
              "trainingName": "%s",
              "trainingTypeName": "Yoga",
              "trainingDate": "%s",
              "trainingDuration": %d
            }
            """.formatted(state.traineeUsername, state.trainerUsername, name, date, duration);

        state.lastResult = mockMvc.perform(post("/trainings")
                        .header("Authorization", "Bearer " + state.jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
    }

    @When("a training is created with trainee {string} and name {string} on {string} for {int} minutes")
    public void createTrainingWithBadTrainee(String traineeUsername, String name, String date, int duration) throws Exception {
        String trainerUsername = state.trainerUsername != null ? state.trainerUsername : "fake.trainer";
        String body = """
            {
              "traineeUsername": "%s",
              "trainerUsername": "%s",
              "trainingName": "%s",
              "trainingTypeName": "Yoga",
              "trainingDate": "%s",
              "trainingDuration": %d
            }
            """.formatted(traineeUsername, trainerUsername, name, date, duration);

        state.lastResult = mockMvc.perform(post("/trainings")
                        .header("Authorization", "Bearer " + state.jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
    }

    @Then("the response status should be {int}")
    public void checkStatus(int expectedStatus) {
        assertEquals(expectedStatus, state.lastResult.getResponse().getStatus());
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