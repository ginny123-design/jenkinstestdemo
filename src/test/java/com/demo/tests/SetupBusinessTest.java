package com.demo.tests;

import com.demo.base.BaseTest;
import com.demo.pages.*;
import com.demo.utils.OtpUtil;
import com.demo.utils.ScreenshotUtil;
import org.testng.annotations.Test;

public class SetupBusinessTest extends BaseTest {

    @Test(enabled = false, timeOut = 120000, description = "Automate Business Setup Persona Auto")
    public void testLoginAndCompleteBusinessSetup() throws Exception {
        FlyLoginPage loginPage = new FlyLoginPage(driver);
        FlyOtpPage otpPage = new FlyOtpPage(driver);
        FlyDashboardPage dashboardPage = new FlyDashboardPage(driver);
        FlyBusinessSetupPage businessPage = new FlyBusinessSetupPage(driver);

        System.out.println("1. Navigating to QA2 Fly platform...");
        loginPage.navigateTo();

        System.out.println("2. Entering credentials & submitting...");
        loginPage.performLogin("ginny+9488qa@forwardai.com", "Test@123");

        String latestOtp = OtpUtil.fetchLatestOtp();

        if (otpPage.isGetNewCodeDisabled()) {
            System.out.println("Get a new code is disabled. Refreshing the page...");
            driver.navigate().refresh();
            Thread.sleep(3000);

            if (loginPage.isEmailInputVisible() || otpPage.isGetNewCodeDisabled()) {
                System.out.println("Logging in completely again...");
                if (!loginPage.isEmailInputVisible()) {
                    loginPage.navigateTo();
                }
                loginPage.performLogin("ginny+9488qa@forwardai.com", "Test@123");
                Thread.sleep(7000);
                latestOtp = OtpUtil.fetchLatestOtp();
            }
        }

        otpPage.enterOtp(latestOtp);
        otpPage.selectTrustThisDevice();
        otpPage.clickVerifyButton();

        ScreenshotUtil.captureScreenshot(driver, "05-otp-verified");

        Thread.sleep(6000);

        System.out.println("3. Opening Sandbox & searching 'Persona Auto'...");
        dashboardPage.clickSandbox();
        dashboardPage.searchAndSelectBusiness("Persona Auto");

        ScreenshotUtil.captureScreenshot(driver, "06-business-selected");

        Thread.sleep(3000);

        System.out.println("4. Accessing Business Settings...");
        businessPage.clickSettingsTab();

        System.out.println("5. Continuing setup flow...");
        businessPage.clickContinue();

        businessPage.handleSandboxPopupIfPresent();

        businessPage.clickContinue();

        System.out.println("6. Filling Job Title...");
        businessPage.fillJobTitle("Manga");

        businessPage.clickContinue();

        businessPage.clickContinue();

        businessPage.clickGetStartedHere();

        System.out.println("7. Filling Business Description...");
        businessPage.fillDescription("Random descriptive text for Persona Auto business.");
        businessPage.clickContinue();

        System.out.println("8. Filling EIN and Physical Address...");
        businessPage.fillEinAndAddress("123456789", "123 Main St");
        businessPage.clickContinue();

        System.out.println("9. Proceeding to Document Upload...");
        businessPage.clickProceed();

        System.out.println("10. Uploading required sample document...");
        businessPage.uploadDocuments("sample.pdf");
        businessPage.clickContinue();

        ScreenshotUtil.captureScreenshot(driver, "07-setup-complete");
        System.out.println("Successfully completed Business Setup Persona Auto flow!");
    }
}
