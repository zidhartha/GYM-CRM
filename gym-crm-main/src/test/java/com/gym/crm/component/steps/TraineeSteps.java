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

public class TraineeSteps {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private SharedState state;


    @When("a trainee is registered with first name {string} and last name {string}")
    public void registerTrainee(String firstName, String lastName) throws Exception {
        String body = """
            {"firstname":"%s","lastname":"%s","dateOfBirth":"2000-01-01","address":"Test St"}
            """.formatted(firstName, lastName);

        state.lastResult = mockMvc.perform(post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
    }

    @Given("a registered trainee with first name {string} and last name {string}")
    public void givenRegisteredTrainee(String firstName, String lastName) throws Exception {
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
        state.traineePassword = json.get("password").asText();
        state.lastRegisteredUsername = state.traineeUsername;
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

    @When("the trainee profile is requested")
    public void getProfile() throws Exception {
        state.lastResult = mockMvc.perform(get("/trainees/" + state.traineeUsername)
                        .header("Authorization", "Bearer " + state.jwtToken))
                .andReturn();
    }

    @When("the trainee profile is requested for username {string}")
    public void getProfileByUsername(String username) throws Exception {
        state.lastResult = mockMvc.perform(get("/trainees/" + username)
                        .header("Authorization", "Bearer " + state.jwtToken))
                .andReturn();
    }

    @When("the trainee profile is updated with first name {string}")
    public void updateTraineeFirstName(String firstName) throws Exception {
        String body = """
        {
          "firstName":"%s",
          "lastName":"Updated",
          "dateOfBirth":"2000-01-01",
          "address":"New St",
          "active":true
        }
        """.formatted(firstName);

        state.lastResult = mockMvc.perform(put("/trainees/" + state.traineeUsername)
                        .header("Authorization", "Bearer " + state.jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
    }

    @When("the trainee profile is deleted")
    public void deleteTraineeProfile() throws Exception {
        state.lastResult = mockMvc.perform(delete("/trainees/" + state.traineeUsername)
                        .header("Authorization", "Bearer " + state.jwtToken))
                .andReturn();
    }

    @When("the trainee status is changed to active")
    public void activateTrainee() throws Exception {
        String body = "{\"isActive\":true}";
        state.lastResult = mockMvc.perform(patch("/trainees/" + state.traineeUsername)
                        .header("Authorization", "Bearer " + state.jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
    }

    @When("the trainee status is changed to inactive")
    public void deactivateTrainee() throws Exception {
        String body = "{\"isActive\":false}";
        state.lastResult = mockMvc.perform(patch("/trainees/" + state.traineeUsername)
                        .header("Authorization", "Bearer " + state.jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
    }
}
