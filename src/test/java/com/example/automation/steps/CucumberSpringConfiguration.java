package com.example.automation.steps;

import com.example.automation.AutomationDashboardApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = AutomationDashboardApplication.class)
public class CucumberSpringConfiguration {
}
