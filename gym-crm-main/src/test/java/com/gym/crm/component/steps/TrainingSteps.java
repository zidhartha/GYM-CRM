package com.gym.crm.component.steps;

import com.gym.crm.component.SharedState;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TrainingSteps {

    @Autowired private MockMvc mockMvc;
    @Autowired private SharedState state;

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
}