package com.gym.crm.integration;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

@Component
@ScenarioScope
public class IntegrationSharedState {
    public MvcResult lastResult;
    public String traineeUsername;
    public String traineePassword;
    public String trainerUsername;
    public String lastRegisteredUsername;
    public String jwtToken;
}
