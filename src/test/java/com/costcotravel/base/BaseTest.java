package com.costcotravel.base;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BaseTest {

    public static final String BASE_URL = "https://www.costcotravel.com/";

    protected ChromeDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    protected void setUp() {
        boolean headless = Boolean.parseBoolean(System.getenv().getOrDefault("HEADLESS", "false"));
        driver = ChromeSupport.launch(ChromeSupport.suiteOptions(headless), true);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get(BASE_URL);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
