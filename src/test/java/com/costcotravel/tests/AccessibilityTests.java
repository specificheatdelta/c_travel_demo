package com.costcotravel.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.costcotravel.base.AxeReport;
import com.costcotravel.base.BaseTest;
import com.costcotravel.pages.HomePage;
import com.costcotravel.pages.LoginPage;
import com.deque.html.axecore.results.Rule;

class AccessibilityTests extends BaseTest {

    @Test
    @DisplayName("Homepage skip link, logo alt text, and search widget labels")
    void homepageHasSkipLinkLogoAltAndSearchLabels() {
        HomePage home = new HomePage(driver).waitUntilLoaded();

        assertTrue(home.isSkipLinkPresent(), "Homepage should expose a Skip to main content link");
        String alt = home.logoAltText();
        assertTrue(alt != null && !alt.isBlank(), "Costco Travel logo image should have alt text");
        home.selectHotelsTab();
        assertTrue(home.isHotelSearchFormVisible(),
                "Hotel destination, dates, and Search should be visible for assistive tech");
    }

    @Test
    @DisplayName("Homepage search widget and header have no serious axe violations")
    void homepageSearchAndHeaderHaveNoSeriousAxeViolations() {
        new HomePage(driver).waitUntilLoaded();

        List<Rule> violations = AxeReport.scan(driver, List.of("header", "#search_widget"));
        AxeReport.write("accessibility-homepage.txt", driver.getTitle(), driver.getCurrentUrl(), violations);

        List<Rule> blocking = AxeReport.blocking(violations);
        assertTrue(blocking.isEmpty(),
                blocking.size() + " serious or critical accessibility violations in the header or search widget. "
                        + "See reports/accessibility-homepage.txt");
    }

    @Test
    @DisplayName("Sign-in form labels the email and password fields")
    void signInFormExposesLabeledFields() {
        new HomePage(driver).waitUntilLoaded().openLogin();
        LoginPage login = new LoginPage(driver).waitUntilReady();

        assertTrue(login.isEmailLabeled(), "Email field should have a visible label");
        assertTrue(login.isPasswordLabeled(), "Password field should have a visible label");
        assertFalse(login.signInButtonText().isBlank(), "Sign-in button should have a visible name");

        List<Rule> violations = AxeReport.scan(driver, List.of());
        AxeReport.write("accessibility-signin.txt", driver.getTitle(), driver.getCurrentUrl(), violations);

        List<Rule> blocking = AxeReport.blocking(violations);
        assertTrue(blocking.isEmpty(),
                blocking.size() + " serious or critical accessibility violations on the sign-in page. "
                        + "See reports/accessibility-signin.txt");
    }
}
