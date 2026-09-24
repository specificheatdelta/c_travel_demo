package com.costcotravel.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.costcotravel.base.BaseTest;
import com.costcotravel.pages.HomePage;

class CostcoTravelHomeTests extends BaseTest {

    @Test
    @DisplayName("Homepage loads with Costco Travel branding and login")
    void homePageLoadsWithLogoAndLogin() {
        HomePage home = new HomePage(driver).waitUntilLoaded();

        assertTrue(home.getTitle().contains("Costco Travel"), "Expected page title to include Costco Travel");
        assertTrue(home.isLogoVisible(), "Costco Travel logo should be visible");
        assertTrue(home.isLoginVisible(), "Login control should be visible");
        assertTrue(home.isHelpCenterVisible(), "Help Center link should be visible");
    }

    @Test
    @DisplayName("Hotels tab shows destination, dates, and Search")
    void hotelsTabShowsSearchFields() {
        HomePage home = new HomePage(driver).waitUntilLoaded();

        home.selectHotelsTab();

        assertTrue(home.isHotelSearchFormVisible(),
                "Hotel destination, check-in, check-out, and Search should be visible");
    }

    @Test
    @DisplayName("Rental Cars tab shows pickup location and Search")
    void rentalCarsTabShowsPickupAndSearch() {
        HomePage home = new HomePage(driver).waitUntilLoaded();

        home.selectRentalCarsTab();

        assertTrue(home.isRentalCarSearchFormVisible(),
                "Pickup location and rental car Search should be visible");
    }

    @Test
    @DisplayName("Cruises tab shows the cruise search form")
    void cruisesTabShowsSearchForm() {
        HomePage home = new HomePage(driver).waitUntilLoaded();

        home.selectCruisesTab();

        assertTrue(home.isCruiseSearchFormVisible(),
                "Cruise destination search field should be visible");
    }
}
