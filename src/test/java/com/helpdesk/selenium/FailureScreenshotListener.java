package com.helpdesk.selenium;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FailureScreenshotListener {

    public static String captureScreenshot(WebDriver driver, String testName) {
        if (driver == null) {
            System.err.println("WebDriver is null. Cannot capture screenshot.");
            return null;
        }

        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "failure_" + testName + "_" + timestamp + ".png";
            String targetDir = "target/screenshots/";
            
            File destinationDir = new File(targetDir);
            if (!destinationDir.exists()) {
                destinationDir.mkdirs();
            }

            File destination = new File(targetDir + fileName);
            FileUtils.copyFile(source, destination);

            System.out.println("Screenshot captured on test failure: " + destination.getAbsolutePath());
            return destination.getAbsolutePath();

        } catch (IOException e) {
            System.err.println("Failed to save failure screenshot: " + e.getMessage());
            return null;
        }
    }
}
