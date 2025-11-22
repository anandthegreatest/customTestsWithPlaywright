package com.example.automation.controller;

import com.example.automation.model.TestStatus;
import com.example.automation.service.TestExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private TestExecutionService testExecutionService;

    /**
     * Serves the main dashboard page.
     * Adds the current list of test statuses to the model for Thymeleaf rendering.
     * 
     * @param model Spring Model object.
     * @return The name of the view template ("index").
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tests", testExecutionService.getAllTestStatuses());
        return "index";
    }

    /**
     * Endpoint to trigger a test run.
     * 
     * @param testName The name of the test to run.
     * @return A confirmation message.
     */
    @PostMapping("/run-test")
    @ResponseBody
    public String runTest(@RequestParam String testName) {
        testExecutionService.runTest(testName);
        return "Started " + testName;
    }

    /**
     * API endpoint to get the current status of all tests.
     * Used by the frontend polling mechanism to update the UI.
     * 
     * @return List of TestStatus objects.
     */
    @GetMapping("/api/status")
    @ResponseBody
    public List<TestStatus> getStatus() {
        return testExecutionService.getAllTestStatuses();
    }

    /**
     * API endpoint to clear all test results.
     * 
     * @return A confirmation message.
     */
    @PostMapping("/api/clear")
    @ResponseBody
    public String clear() {
        testExecutionService.clearResults();
        return "Cleared";
    }

    /**
     * API endpoint for tests to report their status updates.
     * 
     * @param status The new status object sent by the test.
     */
    @PostMapping("/api/status/update")
    @ResponseBody
    public void updateStatus(@RequestBody TestStatus status) {
        testExecutionService.updateTestStatus(status);
    }
}
