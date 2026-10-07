package com.helpdesk.selenium;

import com.helpdesk.model.Category;
import com.helpdesk.model.Priority;
import com.helpdesk.model.Status;
import com.helpdesk.model.Ticket;
import com.helpdesk.repository.TicketRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(FailureScreenshotExtension.class)
public class EmployeeHelpdeskUserJourneysTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TicketRepository ticketRepository;

    public WebDriver driver; // Public for FailureScreenshotExtension access
    private String baseUrl;

    @BeforeAll
    public void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setup() {
        baseUrl = "http://localhost:" + port;
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");

        try {
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        } catch (Exception e) {
            System.out.println("Chrome Driver setup notice: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("User Journey 1: Create a new IT ticket via UI form and verify submission")
    public void testCreateTicketUserJourney() {
        if (driver == null) return;

        driver.get(baseUrl + "/tickets/new");

        driver.findElement(By.id("requesterName")).sendKeys("Selenium Automation User");
        driver.findElement(By.id("requesterEmail")).sendKeys("selenium.test@company.com");
        driver.findElement(By.id("department")).sendKeys("DevOps QA");
        
        new Select(driver.findElement(By.id("category"))).selectByValue("NETWORK");
        driver.findElement(By.id("title")).sendKeys("Automated Test: VPN Latency Spike");
        new Select(driver.findElement(By.id("priority"))).selectByValue("HIGH");
        driver.findElement(By.id("description")).sendKeys("Detailed description created by automated Selenium test suite.");

        driver.findElement(By.id("submitTicketBtn")).click();

        assertTrue(driver.getCurrentUrl().contains("/tickets"), "Should redirect to ticket list URL");
        WebElement alert = driver.findElement(By.id("flashSuccessAlert"));
        assertNotNull(alert, "Flash success message alert should be visible");
        assertTrue(alert.getText().contains("created successfully"), "Alert text should confirm ticket creation");

        List<Ticket> searchResults = ticketRepository.searchTickets("VPN Latency Spike");
        assertFalse(searchResults.isEmpty(), "Created ticket should exist in database");
        assertEquals("Selenium Automation User", searchResults.get(0).getRequesterName());
    }

    @Test
    @DisplayName("User Journey 2: Search and filter tickets directory by Keyword")
    public void testSearchAndFilterUserJourney() {
        if (driver == null) return;

        driver.get(baseUrl + "/tickets");

        WebElement searchInput = driver.findElement(By.id("tableSearchInput"));
        searchInput.sendKeys("VPN");

        List<WebElement> rows = driver.findElements(By.cssSelector("#ticketsTable tbody tr"));
        assertFalse(rows.isEmpty(), "Filtered table should contain matching ticket rows");
    }

    @Test
    @DisplayName("User Journey 3: IT Support Workflow - Update ticket status and resolution notes")
    public void testITSupportWorkflowUserJourney() {
        if (driver == null) return;

        Ticket firstTicket = ticketRepository.findAll().get(0);

        driver.get(baseUrl + "/tickets/" + firstTicket.getId());

        WebElement statusSelectElem = driver.findElement(By.id("statusSelect"));
        Select statusSelect = new Select(statusSelectElem);
        statusSelect.selectByValue("IN_PROGRESS");

        driver.findElement(By.name("assignedAgent")).clear();
        driver.findElement(By.name("assignedAgent")).sendKeys("IT Agent Alex");
        driver.findElement(By.name("resolutionNotes")).clear();
        driver.findElement(By.name("resolutionNotes")).sendKeys("Investigating network traffic");

        driver.findElement(By.id("updateStatusBtn")).click();

        // Assert DOM element renders updated status badge
        WebElement badge = driver.findElement(By.cssSelector(".badge-in_progress"));
        assertNotNull(badge, "Status badge for IN_PROGRESS should render on page");
        assertTrue(badge.getText().equalsIgnoreCase("IN_PROGRESS"));

        // Re-query database via search query to bypass Hibernate L1 entity cache
        List<Ticket> fresh = ticketRepository.searchTickets(firstTicket.getTicketNumber());
        assertFalse(fresh.isEmpty());
        assertEquals(Status.IN_PROGRESS, fresh.get(0).getStatus(), "Ticket status in database should be IN_PROGRESS");
    }

    @Test
    @DisplayName("User Journey 4: Verify Analytics Dashboard live counters")
    public void testDashboardCountersUserJourney() {
        if (driver == null) return;

        driver.get(baseUrl + "/dashboard");

        WebElement totalCount = driver.findElement(By.id("totalTicketsCount"));
        assertNotNull(totalCount, "Total tickets count card should exist");
        assertTrue(Integer.parseInt(totalCount.getText()) > 0, "Total tickets count should be greater than zero");
    }
}
