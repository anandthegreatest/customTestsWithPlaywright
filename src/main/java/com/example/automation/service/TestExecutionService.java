package com.example.automation.service;

import com.example.automation.model.TestStatus;
import com.microsoft.playwright.*;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TestExecutionService {

    private final Map<String, TestStatus> testResults = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    /**
     * Retrieves all current test statuses from the in-memory map.
     * 
     * @return List of TestStatus objects.
     */
    public List<TestStatus> getAllTestStatuses() {
        return new ArrayList<>(testResults.values());
    }

    /**
     * Updates the status of a test. This is typically called by the running test
     * via the API to report progress.
     * 
     * @param status The new status object.
     */
    public void updateTestStatus(TestStatus status) {
        testResults.put(status.getTestName(), status);
    }

    /**
     * Triggers the execution of a specific Cucumber test by name.
     * It launches a Maven process to run the test with the specified tag.
     * The execution is asynchronous.
     * 
     * @param testName The name of the test (used as a Cucumber tag).
     */
    public void runTest(String testName) {
        // Initial status
        TestStatus status = new TestStatus(testName, "PENDING", "Queued...", LocalDateTime.now().toString());
        testResults.put(testName, status);

        executor.submit(() -> {
            try {
                // Determine command based on OS
                boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
                String mavenCmd = isWindows ? "mvn.cmd" : "mvn";

                // Construct command: mvn test -Dcucumber.filter.tags="@tag"
                ProcessBuilder builder = new ProcessBuilder(
                        mavenCmd,
                        "test",
                        "-Dcucumber.filter.tags=@" + testName);
                builder.directory(new java.io.File("f:/agentworkspace"));
                builder.redirectErrorStream(true);

                Process process = builder.start();

                // Read output to log (optional, could stream to UI)
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[Maven]: " + line);
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    status.setStatus("FAILED");
                    status.setMessage("Test execution failed (Exit code " + exitCode + ")");
                } else {
                    TestStatus current = testResults.get(testName);
                    if ("PENDING".equals(current.getStatus()) || "RUNNING".equals(current.getStatus())) {
                        status.setStatus("PASSED"); // Assume pass if maven succeeded
                        status.setMessage("Test execution completed.");
                    }

                    // Generate Report
                    try {
                        java.nio.file.Path screenshotPath = java.nio.file.Paths.get(testName + ".png");
                        if (java.nio.file.Files.exists(screenshotPath)) {
                            byte[] screenshot = java.nio.file.Files.readAllBytes(screenshotPath);
                            generateReport(testName, status.getStatus(), screenshot);
                        }
                    } catch (Exception e) {
                        System.err.println("Error generating report: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                status.setStatus("FAILED");
                status.setMessage("Error launching test: " + e.getMessage());
                e.printStackTrace();
            } finally {
                status.setTimestamp(LocalDateTime.now().toString());
                testResults.put(testName, status);
            }
        });
    }

    /**
     * Generates a Word document report for the executed test.
     * Includes the test name, status, timestamp, and a screenshot.
     * 
     * @param testName   Name of the test.
     * @param status     Final status of the test.
     * @param screenshot Byte array of the screenshot image.
     */
    private void generateReport(String testName, String status, byte[] screenshot) {
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("Test Report: " + testName);
            titleRun.setBold(true);
            titleRun.setFontSize(20);

            XWPFParagraph info = document.createParagraph();
            XWPFRun infoRun = info.createRun();
            infoRun.setText("Status: " + status);
            infoRun.addBreak();
            infoRun.setText("Timestamp: " + LocalDateTime.now());
            infoRun.addBreak();

            if (screenshot != null && screenshot.length > 0) {
                XWPFParagraph image = document.createParagraph();
                image.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun imageRun = image.createRun();
                imageRun.addPicture(new ByteArrayInputStream(screenshot),
                        XWPFDocument.PICTURE_TYPE_PNG,
                        testName + ".png",
                        Units.toEMU(400),
                        Units.toEMU(300));
            }

            try (FileOutputStream out = new FileOutputStream(testName + "_report.docx")) {
                document.write(out);
            }
        } catch (Exception e) {
            System.err.println("Failed to generate report: " + e.getMessage());
        }
    }

    /**
     * Clears all test results from the in-memory map.
     */
    public void clearResults() {
        testResults.clear();
    }
}
