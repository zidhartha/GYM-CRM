package com.gym.crm.component.config;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/component")
@ConfigurationParameters({
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.gym.crm,component.steps"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
})
public class CucumberComponentTest {
}