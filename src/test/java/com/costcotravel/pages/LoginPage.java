package com.costcotravel.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.costcotravel.base.Credentials;

/**
 * Costco sign-in page opened from the Costco Travel header.
 * Callers pass the username and password in; this page does not store them.
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By email = By.id("signInName");
    private final By password = By.id("password");
    private final By signInButton = By.id("next");
    private final By emailLabel = By.cssSelector("label[for='signInName']");
    private final By passwordLabel = By.cssSelector("label[for='password']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public LoginPage waitUntilReady() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(email),
                ExpectedConditions.urlContains("signin.costco.com")));
        if (isCookieBlockVisible()) {
            throw new IllegalStateException(
                    "Sign-in page says the browser is blocking cookies, so the form cannot be used.");
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(email));
        return this;
    }

    public boolean isEmailLabeled() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(emailLabel)).isDisplayed();
    }

    public boolean isPasswordLabeled() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(passwordLabel)).isDisplayed();
    }

    public String signInButtonText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(signInButton)).getText().trim();
    }

    public void signIn(String username, String passwordValue) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(email)).sendKeys(username);
        driver.findElement(password).sendKeys(passwordValue);
        driver.findElement(signInButton).click();
        WebDriverWait signedIn = new WebDriverWait(driver, Duration.ofSeconds(40));
        try {
            signedIn.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("costcotravel.com"),
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".error, #claimVerificationServerError, .error-page"))));
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Sign-in did not finish. " + Credentials.redact(ex.getMessage()), ex);
        }
        if (driver.getCurrentUrl().contains("signin.costco.com")) {
            String pageText = driver.findElement(By.tagName("body")).getText().replaceAll("\\s+", " ").trim();
            if (pageText.length() > 240) {
                pageText = pageText.substring(0, 240);
            }
            throw new IllegalStateException("Sign-in stayed on the Costco sign-in page. " + Credentials.redact(pageText));
        }
    }

    private boolean isCookieBlockVisible() {
        return driver.findElements(By.xpath("//*[contains(.,\"block cookies\")]")).stream()
                .anyMatch(element -> element.isDisplayed() && element.getText().contains("block cookies"));
    }
}
