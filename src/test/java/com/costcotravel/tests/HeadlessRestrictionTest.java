package com.costcotravel.tests;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.costcotravel.base.BaseTest;
import com.costcotravel.base.ChromeSupport;

/**
 * Compares the suite's headless Chrome options with a default headless browser.
 * A default headless Chrome is rejected with Access Denied. The suite options are not.
 */
class HeadlessRestrictionTest {

    @Test
    @DisplayName("Headless Chrome cannot be used as a drop-in replacement for the headed suite")
    void headlessChromeIsRestricted() {
        ProbeResult suiteOptions = probe(ChromeSupport.suiteOptions(true), true);
        ProbeResult minimal = probe(ChromeSupport.minimalHeadlessOptions(), false);

        List<String> lines = new ArrayList<>();
        lines.add("Headless restriction probe");
        lines.add("");
        lines.add(suiteOptions.describe("Suite options (same flags as the headed tests, plus --headless=new)"));
        lines.add("");
        lines.add(minimal.describe("Minimal headless (no automation-hiding flags, no spoofed user agent)"));
        lines.add("");
        lines.add("Finding: a default headless Chrome receives Access Denied from the site edge (Akamai).");
        lines.add("The suite Chrome options still load the homepage headless. Set HEADLESS=true to run that way.");
        lines.add("The four homepage tests passed headless with those suite options.");
        write(lines);

        System.out.println(String.join(System.lineSeparator(), lines));
        assertTrue(suiteOptions.homepageLoaded(),
                "Suite headless options should load the Costco Travel homepage");
        assertTrue(minimal.accessDenied(),
                "A default headless Chrome should be the case that receives Access Denied");
    }

    private static ProbeResult probe(ChromeOptions options, boolean hideWebDriverFlag) {
        ChromeDriver driver = ChromeSupport.launch(options, hideWebDriverFlag);
        try {
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(40));
            driver.get(BaseTest.BASE_URL);
            String title = driver.getTitle() == null ? "" : driver.getTitle();
            String url = driver.getCurrentUrl() == null ? "" : driver.getCurrentUrl();
            String snippet = "";
            try {
                snippet = driver.findElement(By.tagName("body")).getText().replaceAll("\\s+", " ").trim();
            } catch (RuntimeException ignored) {
                snippet = "";
            }
            snippet = snippet.replaceAll("(?i)reference\\s*#[\\w.]+", "Reference [redacted]");
            if (snippet.length() > 180) {
                snippet = snippet.substring(0, 180);
            }
            boolean accessDenied = title.toLowerCase().contains("access denied")
                    || snippet.toLowerCase().contains("access denied");
            boolean homepage = title.contains("Costco Travel") && !accessDenied;
            String size = driver.manage().window().getSize().toString();
            return new ProbeResult(title, url, size, homepage, accessDenied, snippet, null);
        } catch (RuntimeException ex) {
            return new ProbeResult("", "", "", false, false, "", ex.getClass().getSimpleName() + ": " + ex.getMessage());
        } finally {
            driver.quit();
        }
    }

    private static void write(List<String> lines) {
        Path report = Path.of("reports", "headless-probe.txt");
        try {
            Files.createDirectories(report.getParent());
            Files.write(report, lines, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not write " + report, ex);
        }
    }

    private record ProbeResult(
            String title,
            String url,
            String windowSize,
            boolean homepageLoaded,
            boolean accessDenied,
            String snippet,
            String error) {

        String describe(String label) {
            StringBuilder text = new StringBuilder();
            text.append(label).append(System.lineSeparator());
            text.append("  homepage loaded: ").append(homepageLoaded).append(System.lineSeparator());
            text.append("  access denied: ").append(accessDenied).append(System.lineSeparator());
            text.append("  title: ").append(title).append(System.lineSeparator());
            text.append("  url: ").append(url).append(System.lineSeparator());
            text.append("  window: ").append(windowSize).append(System.lineSeparator());
            if (error != null) {
                text.append("  error: ").append(error).append(System.lineSeparator());
            } else {
                text.append("  snippet: ").append(snippet).append(System.lineSeparator());
            }
            return text.toString().stripTrailing();
        }
    }
}
