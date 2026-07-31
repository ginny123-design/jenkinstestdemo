package com.demo.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ScreenshotUtil {

    private static final String SCREENSHOT_DIR = "test-results/screenshots";

    public static String captureScreenshot(WebDriver driver, String fileName) {
        if (driver == null) {
            System.out.println("Driver instance is null. Cannot capture screenshot.");
            return null;
        }

        try {
            Path outputDir = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destPath = outputDir.resolve(fileName.endsWith(".png") ? fileName : fileName + ".png");
            Files.copy(srcFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Screenshot saved: " + destPath.toAbsolutePath());
            return destPath.toAbsolutePath().toString();
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
}
