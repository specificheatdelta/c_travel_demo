package com.costcotravel.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.costcotravel.base.BaseTest;
import com.costcotravel.base.Credentials;
import com.costcotravel.pages.HomePage;
import com.costcotravel.pages.LoginPage;

class MemberSignInTests extends BaseTest {

    @Override
    @BeforeEach
    protected void setUp() {
        Assumptions.assumeTrue(Credentials.arePresent(),
                "Skipped. Set COSTCO_USERNAME and COSTCO_PASSWORD, or create gitignored credentials.properties. "
                        + "Do not commit either value.");
        super.setUp();
    }

    @Test
    @DisplayName("Member can sign in when credentials are supplied outside the repo")
    void memberCanSignIn() {
        new HomePage(driver).waitUntilLoaded().openLogin();
        new LoginPage(driver).waitUntilReady().signIn(Credentials.username(), Credentials.password());

        assertTrue(new HomePage(driver).isSignedIn(),
                "After sign-in the header should show the member account control");
    }
}
