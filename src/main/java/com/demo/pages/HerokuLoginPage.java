package com.demo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HerokuLoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By usernameInput = By.id("username");
    private final By passwordInput = By.id("password");
    private final By submitButton = By.cssSelector("button[type='submit']");
    private final By flashBanner = By.id("flash");

    public HerokuLoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void navigateTo() {
        driver.get("https://the-internet.herokuapp.com/login");
    }

    public void enterUsername(String username) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameInput));
        element.clear();
        element.sendKeys(username);
    }

    public void enterPassword(String password) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        element.clear();
        element.sendKeys(password);
    }

    public void clickSubmit() {
        wait.until(ExpectedConditions.elementToBeClickable(submitButton)).click();
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickSubmit();
    }

    public String getFlashMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(flashBanner)).getText();
    }
}
