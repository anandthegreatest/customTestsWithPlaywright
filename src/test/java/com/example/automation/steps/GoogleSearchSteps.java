package com.example.automation.steps;

import com.microsoft.playwright.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;

public class GoogleSearchSteps {

    private Playwright playwright;
    private Browser browser;
    private Page page;

    /**
     * Setup method to initialize Playwright and Browser before each scenario.
     * It also reports the initial status as RUNNING.
     */
    @Before
    public void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        reportStatus("RUNNING", "Test started");
    }

    /**
     * Teardown method to close the browser and Playwright after each scenario.
     * This ensures resources are released.
     */
    @After
    public void teardown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    /**
     * Step definition for "Given I open the Google homepage".
     * Navigates the browser to google.com and reports status.
     */
    @Given("I open the Google homepage")
    public void i_open_the_google_homepage() {
        page.navigate("https://www.google.com");
        reportStatus("RUNNING", "Opened Google Homepage");
    }

    /**
     * Step definition for "When I search for {string}".
     * Enters the query into the search box and presses Enter.
     * 
     * @param query The search term provided in the feature file.
     */
    @When("I search for {string}")
    public void i_search_for(String query) {
        page.fill("textarea[name='q']", query);
        page.press("textarea[name='q']", "Enter");
        page.waitForLoadState();
        reportStatus("RUNNING", "Searched for " + query);
    }

    /**
     * Step definition for "Then I should see results containing {string}".
     * Verifies that the page title contains the expected term.
     * Takes a screenshot and reports the final PASSED status.
     * 
     * @param term The expected term in the results/title.
     */
    @Then("I should see results containing {string}")
    public void i_should_see_results_containing(String term) {
        String title = page.title();
        Assertions.assertTrue(title.contains(term));

        // Take screenshot
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("google-search.png")));
        reportStatus("PASSED", "Found results for " + term);
    }

    /**
     * Helper method to send status updates to the Dashboard application.
     * Constructs a JSON payload and sends a POST request to /api/status/update.
     * 
     * @param status  The status of the test (e.g., RUNNING, PASSED).
     * @param message A descriptive message about the current step.
     */
    private void reportStatus(String status, String message) {
        try {
            String json = String.format(
                    "{\"testName\":\"google-search\",\"status\":\"%s\",\"message\":\"%s\",\"timestamp\":\"%s\"}",
                    status, message, java.time.LocalDateTime.now().toString());

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/status/update"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Failed to report status: " + e.getMessage());
        }
    }
}
