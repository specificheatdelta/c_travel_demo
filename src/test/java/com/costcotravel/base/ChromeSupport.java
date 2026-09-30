package com.costcotravel.base;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Shared Chrome startup used by the homepage suite and the headless probe.
 */
public final class ChromeSupport {

    private ChromeSupport() {
    }

    public static ChromeOptions suiteOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments(
                "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/172.16.1.4 Safari/537.36");
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        return options;
    }

    /** Headless Chrome without the suite's automation-hiding flags. */
    public static ChromeOptions minimalHeadlessOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-notifications");
        return options;
    }

    public static ChromeDriver launch(ChromeOptions options, boolean hideWebDriverFlag) {
        ChromeDriver driver = new ChromeDriver(options);
        if (hideWebDriverFlag) {
            driver.executeCdpCommand(
                    "Page.addScriptToEvaluateOnNewDocument",
                    Map.of("source", "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"));
        }
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(40));
        return driver;
    }
}
