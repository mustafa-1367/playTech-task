import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlaytechTest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static StringBuilder output;

    // Task 1: Setup and open the Playtech Careers website
    @BeforeAll
    public static void setup() {
        WebDriverManager.chromedriver().setup(); // Set up ChromeDriver using WebDriverManager
        driver = new ChromeDriver();             // Launch Chrome
        driver.manage().window().maximize();     // Maximize browser window
        wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Explicit wait
        output = new StringBuilder();            // For collecting all test output
        driver.get("https://www.playtechpeople.com"); // Navigate to Playtech site
    }

    // Task 2: List all locations under the "Locations" tab
    @Test
    @Order(1)
    public void testLocations() {
        try {
            // Click on the "Locations" tab
            WebElement locationsTab = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Locations")));
            locationsTab.click();

            // Wait for the locations to be visible and collect them
            List<WebElement> locationTitles = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("h3.locations__item-title")));

            output.append("Locations found: ").append(locationTitles.size()).append("\n");
            for (WebElement location : locationTitles) {
                output.append(location.getText()).append("\n");
            }
        } catch (Exception e) {
            output.append("Error in testLocations: ").append(e.getMessage()).append("\n");
        }
    }

    // Task 3: Under "Life at Playtech", find and print the Casino unit description
    @Test
    @Order(2)
    public void testCasinoDescription() {
        try {
            // Navigate to "Life at Playtech"
            WebElement lifeTab = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Life at Playtech")));
            lifeTab.click();

            // Click on the "Casino" link
            WebElement casinoLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href*='casino']")));
            casinoLink.click();

            // Retrieve the Casino unit description
            WebElement desc = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".unit-description, .text-block")));

            output.append("\nCasino Unit Description:\n").append(desc.getText()).append("\n");

        } catch (Exception e) {
            output.append("Error in testCasinoDescription: ").append(e.getMessage()).append("\n");
        }
    }

    // Task 4: Under "All Jobs", print jobs that match Estonia-based and specific roles
    @Test
    @Order(3)
    public void testEstoniaJobs() {
        try {
            // Navigate to "All Jobs"
            WebElement jobsLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("All Jobs")));
            jobsLink.click();
            Thread.sleep(5000); // Allow job listings to load

            // Collect all job listings
            List<WebElement> jobs = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a[href*='/job/']")));

            output.append("\nJobs in Estonia (Only relevant listings):\n");
            for (WebElement job : jobs) {
                try {
                    // Extract job location and title
                    String location = job.findElement(By.cssSelector(".job-location")).getText().toLowerCase();
                    String title = job.findElement(By.cssSelector(".job-title")) != null ? job.findElement(By.cssSelector(".job-title")).getText() : "";
                    String link = job.getAttribute("href");

                    // Filter for Estonia-based jobs with specific roles
                    if (location.contains("estonia") && (title.toLowerCase().contains("java") || title.toLowerCase().contains("support") || title.toLowerCase().contains("project"))) {
                        output.append(title).append(" - ").append(location).append(" - ").append(link).append("\n");
                    }
                } catch (NoSuchElementException ignored) {
                }
            }
        } catch (Exception e) {
            output.append("Error in testEstoniaJobs: ").append(e.getMessage()).append("\n");
        }
    }

    // Bonus Task 1: Click by coordinates using Actions class
    @Test
    @Order(4)
    public void bonusClickByCoordinates() {
        try {
            // Simulate a user click by screen coordinates (you can change the x/y values)
            Actions actions = new Actions(driver);
            actions.moveByOffset(100, 200).click().perform();
            output.append("Bonus: Clicked at coordinates (100, 200)\n");
        } catch (Exception e) {
            output.append("Error in bonusClickByCoordinates: ").append(e.getMessage()).append("\n");
        }
    }

    // Task 5: After all tests, close the browser and save the test results to a file
    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit(); // Close browser window
        }
        try (FileWriter writer = new FileWriter("playtech_output.txt")) {
            writer.write(output.toString()); // Save all results to a file
            System.out.println("Test results written to playtech_output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
