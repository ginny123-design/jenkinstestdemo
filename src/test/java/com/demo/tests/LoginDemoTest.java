package com.demo.tests;

import com.demo.base.BaseTest;
import com.demo.pages.HerokuLoginPage;
import com.demo.pages.HerokuSecureAreaPage;
import com.demo.utils.ScreenshotUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginDemoTest extends BaseTest {

    @Test(description = "Successful Login Scenario - Visual Verification")
    public void testSuccessfulLoginScenario() {
        HerokuLoginPage loginPage = new HerokuLoginPage(driver);
        HerokuSecureAreaPage secureAreaPage = new HerokuSecureAreaPage(driver);

        System.out.println("1. Navigating to sample login page...");
        loginPage.navigateTo();
        ScreenshotUtil.captureScreenshot(driver, "01-login-page");

        System.out.println("2. Entering valid credentials...");
        loginPage.enterUsername("tomsmith");
        loginPage.enterPassword("SuperSecretPassword!");
        ScreenshotUtil.captureScreenshot(driver, "02-credentials-entered");

        System.out.println("3. Submitting login form...");
        loginPage.clickSubmit();

        System.out.println("4. Verifying successful login dashboard...");
        Assert.assertTrue(secureAreaPage.getFlashMessage().contains("You logged into a secure area!"),
                "Flash banner text did not match expected success message.");
        Assert.assertTrue(secureAreaPage.getHeadingText().contains("Secure Area"),
                "Heading text did not contain 'Secure Area'.");

        ScreenshotUtil.captureScreenshot(driver, "03-login-success-dashboard");
        System.out.println("Successfully logged in and captured visual evidence!");
    }

    @Test(description = "Invalid Credentials Scenario - Error State Verification")
    public void testInvalidCredentialsScenario() {
        HerokuLoginPage loginPage = new HerokuLoginPage(driver);

        System.out.println("1. Navigating to sample login page...");
        loginPage.navigateTo();

        System.out.println("2. Entering invalid password...");
        loginPage.enterUsername("tomsmith");
        loginPage.enterPassword("WrongPassword123!");

        System.out.println("3. Submitting login form...");
        loginPage.clickSubmit();

        System.out.println("4. Verifying error banner display...");
        Assert.assertTrue(loginPage.getFlashMessage().contains("Your password is invalid!"),
                "Flash banner did not indicate invalid password.");

        ScreenshotUtil.captureScreenshot(driver, "04-login-error-state");
        System.out.println("Successfully verified invalid login error handling & captured screenshot!");
    }
}
