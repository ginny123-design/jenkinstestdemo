package com.demo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class FlyDashboardPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By sandboxOption = By.xpath("(//*[contains(translate(text(),'SANDBOX','sandbox'),'sandbox')])[1]");
    private final By searchInput = By.cssSelector("input[placeholder*='search' i], input[type='search'], .search-box input");
    private final By personaAutoBusiness = By.xpath("(//*[text()='Persona Auto'])[last()]");

    public FlyDashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void clickSandbox() {
        WebElement sandbox = wait.until(ExpectedConditions.visibilityOfElementLocated(sandboxOption));
        sandbox.click();
    }

    public void searchAndSelectBusiness(String businessName) {
        WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        search.clear();
        search.sendKeys(businessName);
        
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {}

        WebElement businessItem = wait.until(ExpectedConditions.visibilityOfElementLocated(personaAutoBusiness));
        businessItem.click();
        System.out.println("Clicked " + businessName + " business successfully. Navigating inside.");
    }
}
