package com.example.automation.model;

public class TestStatus {
    private String testName;
    private String status; // PENDING, RUNNING, PASSED, FAILED
    private String message;
    private String timestamp;

    public TestStatus() {
    }

    public TestStatus(String testName, String status, String message, String timestamp) {
        this.testName = testName;
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
