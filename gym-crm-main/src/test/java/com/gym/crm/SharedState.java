package com.gym.crm;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

@Component
@ScenarioScope
public class SharedState {
    public MvcResult lastResult;
    public String traineeUsername;
    public String traineePassword;
    public String trainerUsername;
    public String lastRegisteredUsername;
    public String jwtToken;
}
