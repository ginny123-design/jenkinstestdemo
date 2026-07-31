package com.demo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HerokuSecureAreaPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By heading = By.tagName("h2");
    private final By flashBanner = By.id("flash");

    public HerokuSecureAreaPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String getHeadingText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(heading)).getText();
    }

    public String getFlashMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(flashBanner)).getText();
    }
}
