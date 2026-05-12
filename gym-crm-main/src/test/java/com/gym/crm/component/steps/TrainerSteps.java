package com.gym.crm.component.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.SharedState;
import com.gym.crm.service.JwtService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class TrainerSteps {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private SharedState state;
    @Autowired private JwtService jwtService;

    @When("a trainer is registered with first name {string} and last name {string} specializing in {string}")
    public void registerTrainer(String firstName, String lastName, String specialization) throws Exception {
        String body = """
            {"firstname":"%s","lastname":"%s","specialization":"%s"}
            """.formatted(firstName, lastName, specialization);

        state.lastResult = mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
    }

    @Given("a registered trainer with first name {string} and last name {string} specializing in {string}")
    public void givenRegisteredTrainer(String firstName, String lastName, String specialization) throws Exception {
        state.lastRegisteredUsername = state.trainerUsername;
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

    @Given("the trainer is authenticated")
    public void authenticateTrainer() {
        state.jwtToken = jwtService.generateToken(state.trainerUsername);
    }


    @When("the trainer profile is requested")
    public void getTrainerProfile() throws Exception {
        state.lastResult = mockMvc.perform(get("/trainers/" + state.trainerUsername)
                        .header("Authorization", "Bearer " + state.jwtToken))
                .andReturn();
    }

    @When("the trainer profile is requested for username {string}")
    public void getTrainerByUsername(String username) throws Exception {
        state.lastResult = mockMvc.perform(get("/trainers/" + username)
                        .header("Authorization", "Bearer " + state.jwtToken))
                .andReturn();
    }

    @When("the trainer profile is updated with first name {string}")
    public void updateTrainerFirstName(String firstName) throws Exception {
        String body = """
        {
          "firstName":"%s",
          "lastName":"Updated",
          "isActive":true
        }
        """.formatted(firstName);

        state.lastResult = mockMvc.perform(put("/trainers/" + state.trainerUsername)
                        .header("Authorization", "Bearer " + state.jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
    }

    @When("the trainer status is changed to active")
    public void activateTrainer() throws Exception {
        String body = "{\"isActive\":true}";
        state.lastResult = mockMvc.perform(patch("/trainers/" + state.trainerUsername)
                        .header("Authorization", "Bearer " + state.jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
    }

    @When("the trainer status is changed to inactive")
    public void deactivateTrainer() throws Exception {
        String body = "{\"isActive\":false}";
        state.lastResult = mockMvc.perform(patch("/trainers/" + state.trainerUsername)
                        .header("Authorization", "Bearer " + state.jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
    }

    @When("unassigned trainers are requested for the trainee")
    public void getUnassignedTrainersForTrainee() throws Exception {
        state.lastResult = mockMvc.perform(get("/trainees/" + state.traineeUsername + "/unassigned-trainers")
                        .header("Authorization", "Bearer " + state.jwtToken))
                .andReturn();
    }

}
