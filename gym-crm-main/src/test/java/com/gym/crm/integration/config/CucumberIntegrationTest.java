package com.gym.crm.integration.config;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/integration")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.gym.crm,com.gym.crm.integration")
public class CucumberIntegrationTest {
}