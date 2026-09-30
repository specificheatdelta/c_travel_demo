package com.costcotravel.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object for https://www.costcotravel.com/
 * Locators were taken from the live homepage search widget and header.
 */
public class HomePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By logo = By.cssSelector("a.logo");
    private final By logoImage = By.cssSelector("a.logo img");
    private final By skipLink = By.id("skip-to-main-link");
    private final By loginLink = By.xpath("//a[contains(normalize-space(),'Login')] | //span[contains(normalize-space(),'Login')]");
    private final By desktopLogin = By.id("yourItineraryMemberAccount");
    private final By signedInIcon = By.cssSelector("[data-test='loggedInIcon']");
    private final By helpCenterLink = By.linkText("Help Center");
    private final By searchWidget = By.id("search_widget");

    private final By hotelsTab = By.id("hotels-tab-id");
    private final By cruisesTab = By.id("cruises-tab-id");
    private final By rentalCarsTab = By.id("rental-cars-tab-id");

    private final By hotelDestination = By.id("hotelDestination");
    private final By checkInDate = By.id("checkInDateWidget");
    private final By checkOutDate = By.id("checkOutDateWidget");
    private final By hotelSearchButton = By.cssSelector("#hotel_2_search_widget_form button.hotel-submit");

    private final By pickupLocation = By.id("pickupLocationTextWidget");
    private final By findCarButton = By.id("findMyCarButton");

    private final By cruiseDestination = By.id("destination_cruiseLine_ship_port");
    private final By cruiseSearchForm = By.id("search_cruises_form");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public HomePage waitUntilLoaded() {
        wait.until(ExpectedConditions.titleContains("Costco Travel"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchWidget));
        return this;
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public boolean isLogoVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(logo)).isDisplayed();
    }

    public String logoAltText() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(logoImage)).getDomAttribute("alt");
    }

    public boolean isSkipLinkPresent() {
        return !driver.findElements(skipLink).isEmpty();
    }

    public HomePage openLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(desktopLogin)).click();
        return this;
    }

    public boolean isSignedIn() {
        return wait.until(driver -> driver.findElements(signedInIcon).stream().anyMatch(WebElement::isDisplayed)
                || driver.findElements(By.cssSelector("[data-test='linkLogout']")).stream().anyMatch(WebElement::isDisplayed));
    }

    public boolean isLoginVisible() {
        return wait.until(driver -> driver.findElements(loginLink).stream().anyMatch(WebElement::isDisplayed));
    }

    public boolean isHelpCenterVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(helpCenterLink)).isDisplayed();
    }

    public HomePage selectHotelsTab() {
        clickTab(hotelsTab);
        wait.until(ExpectedConditions.visibilityOfElementLocated(hotelDestination));
        return this;
    }

    public HomePage selectRentalCarsTab() {
        clickTab(rentalCarsTab);
        wait.until(ExpectedConditions.visibilityOfElementLocated(pickupLocation));
        return this;
    }

    public HomePage selectCruisesTab() {
        clickTab(cruisesTab);
        wait.until(ExpectedConditions.visibilityOfElementLocated(cruiseSearchForm));
        return this;
    }

    public boolean isHotelSearchFormVisible() {
        return isDisplayed(hotelDestination)
                && isDisplayed(checkInDate)
                && isDisplayed(checkOutDate)
                && isDisplayed(hotelSearchButton);
    }

    public boolean isRentalCarSearchFormVisible() {
        return isDisplayed(pickupLocation) && isDisplayed(findCarButton);
    }

    public boolean isCruiseSearchFormVisible() {
        return isDisplayed(cruiseSearchForm) && isDisplayed(cruiseDestination);
    }

    private void clickTab(By tab) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(tab));
        element.click();
    }

    private boolean isDisplayed(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
    }
}
