package com.gym.crm.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;



import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.gym.crm")
@PropertySource("classpath:application.properties")
@Import({PersistenceConfig.class, SecurityConfig.class})
public class AppConfig {
}
