# Spring Boot Test Automation App

This project is a Spring Boot application designed to manage and execute Playwright tests with a Thymeleaf-based UI.

## Features implemented so far:
- **Spring Boot Backend**: Setup with Web and Thymeleaf starters.
- **Playwright Integration**: Basic service to launch browsers and run tests.
- **Dashboard UI**: A dark-themed, responsive UI to trigger tests and view real-time status.
- **Test Execution**: Asynchronous test execution with status updates.
- **Report Generation**: Automatically generates a Word document (.docx) with test status and screenshots.

## Prerequisites
- Java 17+
- Maven

## How to Run
1. **Install Dependencies**:
   ```bash
   mvn clean install
   ```
2. **Install Playwright Browsers** (First time only):
   ```bash
   mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
   ```
3. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```
4. **Access the Dashboard**:
   Open [http://localhost:8080](http://localhost:8080) in your browser.
5. **Run Cucumber Tests**:
   Click "Run Google Search Test" to execute the Cucumber feature `google_search.feature`.

## Project Structure
- `src/main/java/com/example/automation`: Core Java code.
  - `controller`: Web controllers.
  - `service`: Business logic for running tests.
  - `model`: Data models.
- `src/main/resources/templates`: Thymeleaf templates (HTML).
- `src/main/resources/static`: Static assets (CSS, JS).

## Next Steps
- Implement Cucumber integration for structured BDD tests.
- Add Word document generation for test reports.
- Enhance the UI with more detailed logs and history.
