package com.demo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FlyLoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By emailInput = By.id("email-input");
    private final By continueButton = By.xpath("//button[contains(text(),'Continue')]");
    private final By passwordInput = By.id("password-input");

    public FlyLoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void navigateTo() {
        driver.get("https://qa2.fly.test4wd.com");
    }

    public boolean isEmailInputVisible() {
        try {
            return driver.findElement(emailInput).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void enterEmail(String email) {
        WebElement emailEl = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        emailEl.clear();
        emailEl.sendKeys(email);
    }

    public void clickContinue() {
        wait.until(ExpectedConditions.elementToBeClickable(continueButton)).click();
    }

    public void enterPasswordAndSubmit(String password) {
        WebElement passEl = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        passEl.click();
        passEl.sendKeys(password);
        passEl.sendKeys(Keys.ENTER);
    }

    public void performLogin(String email, String password) {
        enterEmail(email);
        clickContinue();
        enterPasswordAndSubmit(password);
    }
}
