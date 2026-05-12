package com.gym.crm.component.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.SharedState;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;


public class CommonSteps {

    @Autowired
    private SharedState state;
    @Autowired
    private ObjectMapper objectMapper;

    @Then("the response status should be {int}")
    public void checkStatus(int expectedStatus) {
        assertEquals(expectedStatus, state.lastResult.getResponse().getStatus());
    }

    @Then("the response should contain a username and password")
    public void checkCredentials() throws Exception {
        String json = state.lastResult.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        assertNotNull(node.get("username"));
        assertNotNull(node.get("password"));
        assertFalse(node.get("username").asText().isBlank());
        assertFalse(node.get("password").asText().isBlank());
    }

    @Then("the profile first name should be {string}")
    public void checkFirstName(String expected) throws Exception {
        String json = state.lastResult.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        assertEquals(expected, node.get("firstName").asText());
    }

    @Then("the profile last name should be {string}")
    public void checkLastName(String expected) throws Exception {
        String json = state.lastResult.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        assertEquals(expected, node.get("lastName").asText());
    }
}