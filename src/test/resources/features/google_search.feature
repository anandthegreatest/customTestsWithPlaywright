Feature: Google Search
  As a user
  I want to search on Google
  So that I can find information

  @google-search
  Scenario: Search for Playwright
    Given I open the Google homepage
    When I search for "Playwright Java"
    Then I should see results containing "Playwright"
