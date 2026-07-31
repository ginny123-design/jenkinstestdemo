package com.demo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class FlyOtpPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By otpInputs = By.cssSelector("input[type='text'], input[type='number']");
    private final By trustDeviceText = By.xpath("//*[contains(text(),'Trust this device')]");
    private final By checkboxInput = By.cssSelector("input[type='checkbox']");
    private final By verifyButton = By.xpath("(//button[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'continue') or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'verify') or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submit')])[last()]");

    public FlyOtpPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public boolean isGetNewCodeDisabled() {
        try {
            List<WebElement> elements = driver.findElements(By.xpath("//*[contains(text(),'Get a new code')]"));
            if (!elements.isEmpty()) {
                WebElement el = elements.get(elements.size() - 1);
                String disabledAttr = el.getAttribute("disabled");
                String classAttr = el.getAttribute("class");
                return disabledAttr != null || (classAttr != null && (classAttr.contains("disabled") || classAttr.contains("pointer-events-none")));
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public void enterOtp(String otpCode) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}

        List<WebElement> inputs = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(otpInputs));
        if (inputs.size() >= 6 && otpCode.length() >= 6) {
            for (int i = 0; i < Math.min(inputs.size(), otpCode.length()); i++) {
                WebElement input = inputs.get(i);
                input.click();
                input.sendKeys(String.valueOf(otpCode.charAt(i)));
            }
        } else if (!inputs.isEmpty()) {
            WebElement firstInput = inputs.get(0);
            firstInput.clear();
            firstInput.sendKeys(otpCode);
        }
    }

    public void selectTrustThisDevice() {
        try {
            List<WebElement> textLabels = driver.findElements(trustDeviceText);
            if (!textLabels.isEmpty() && textLabels.get(0).isDisplayed()) {
                System.out.println("Found 'Trust this device' label, clicking...");
                textLabels.get(0).click();
            } else {
                List<WebElement> checkboxes = driver.findElements(checkboxInput);
                if (!checkboxes.isEmpty()) {
                    System.out.println("Clicking checkbox directly...");
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].click();", checkboxes.get(0));
                }
            }
        } catch (Exception e) {
            System.err.println("Trust this device selection skipped: " + e.getMessage());
        }
    }

    public void clickVerifyButton() {
        try {
            Thread.sleep(1000);
            By fallbackBtn = By.xpath("(//button[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'continue') or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'verify') or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submit')])[last()]");
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(fallbackBtn));
            btn.click();
            System.out.println("Successfully clicked OTP continue/verify button!");
        } catch (Exception e) {
            System.err.println("Clicking verify button using JS fallback...");
            JavascriptExecutor js = (JavascriptExecutor) driver;
            By fallbackBtn = By.xpath("(//button)[last()]");
            js.executeScript("arguments[0].click();", driver.findElement(fallbackBtn));
        }
    }
}
