package com.demo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.List;

public class FlyBusinessSetupPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By settingsTab = By.xpath("(//*[text()='Settings'])[1]");
    private final By continueButton = By.xpath("(//button[contains(text(),'Continue') or contains(text(),'continue')] | //a[contains(text(),'Continue') or contains(text(),'continue')])[1]");
    private final By sandboxWarning = By.xpath("//*[contains(translate(text(),'SANDBOX ENVIRONMENT','sandbox environment'),'sandbox environment')]");
    private final By closePopupButton = By.cssSelector("button[aria-label*='lose' i], button > svg, svg.lucide-x");
    private final By jobInput = By.cssSelector("input[name*='job' i], input[placeholder*='job' i], input[type='text']");
    private final By getStartedBtn = By.xpath("(//button[contains(text(),'Get started here')] | //a[contains(text(),'Get started here')])[last()]");
    private final By descriptionInput = By.cssSelector("textarea, input[placeholder*='description' i]");
    private final By einInput = By.cssSelector("input[placeholder*='EIN' i], input[name*='ein' i]");
    private final By addressInput = By.cssSelector("input[placeholder*='address' i], input[placeholder*='Address' i]");
    private final By proceedButton = By.xpath("(//button[contains(text(),'Proceed')] | //a[contains(text(),'Proceed')])[1]");
    private final By fileInputs = By.cssSelector("input[type='file']");

    public FlyBusinessSetupPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void clickSettingsTab() {
        try {
            WebElement settings = wait.until(ExpectedConditions.visibilityOfElementLocated(settingsTab));
            settings.click();
        } catch (Exception e) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(By.xpath("//*[contains(text(),'Settings')]")));
        }
    }

    public void clickContinue() {
        try {
            Thread.sleep(1500);
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(continueButton));
            btn.click();
        } catch (Exception e) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement btn = driver.findElement(continueButton);
            js.executeScript("arguments[0].click();", btn);
        }
    }

    public void handleSandboxPopupIfPresent() {
        try {
            List<WebElement> warnings = driver.findElements(sandboxWarning);
            if (!warnings.isEmpty() && warnings.get(0).isDisplayed()) {
                System.out.println("Sandbox popup found! Attempting to close...");
                List<WebElement> closeBtns = driver.findElements(closePopupButton);
                if (!closeBtns.isEmpty()) {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].click();", closeBtns.get(0));
                    Thread.sleep(1000);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void fillJobTitle(String jobTitle) {
        try {
            Thread.sleep(1000);
            List<WebElement> jobs = driver.findElements(jobInput);
            if (!jobs.isEmpty()) {
                WebElement jobEl = jobs.get(jobs.size() - 1);
                jobEl.click();
                jobEl.clear();
                jobEl.sendKeys(jobTitle);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.err.println("Error entering Job Title: " + e.getMessage());
        }
    }

    public void clickGetStartedHere() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(getStartedBtn));
            btn.click();
        } catch (Exception ignored) {
        }
    }

    public void fillDescription(String text) {
        try {
            WebElement desc = wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionInput));
            desc.clear();
            desc.sendKeys(text);
        } catch (Exception e) {
            System.err.println("Error filling description: " + e.getMessage());
        }
    }

    public void fillEinAndAddress(String ein, String address) {
        try {
            WebElement einEl = wait.until(ExpectedConditions.visibilityOfElementLocated(einInput));
            einEl.clear();
            einEl.sendKeys(ein);

            WebElement addrEl = wait.until(ExpectedConditions.visibilityOfElementLocated(addressInput));
            addrEl.clear();
            addrEl.sendKeys(address);
            Thread.sleep(2000);
            addrEl.sendKeys(Keys.ARROW_DOWN);
            addrEl.sendKeys(Keys.ENTER);
        } catch (Exception e) {
            System.err.println("Error filling EIN / Address: " + e.getMessage());
        }
    }

    public void clickProceed() {
        try {
            WebElement proceed = wait.until(ExpectedConditions.elementToBeClickable(proceedButton));
            proceed.click();
        } catch (Exception e) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(proceedButton));
        }
    }

    public void uploadDocuments(String relativeFilePath) {
        File uploadFile = new File(relativeFilePath);
        String absolutePath = uploadFile.getAbsolutePath();

        List<WebElement> inputs = driver.findElements(fileInputs);
        for (WebElement input : inputs) {
            try {
                input.sendKeys(absolutePath);
            } catch (Exception e) {
                System.err.println("File upload error: " + e.getMessage());
            }
        }
    }
}
