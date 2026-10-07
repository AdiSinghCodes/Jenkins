package com.helpdesk.selenium;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Field;

public class FailureScreenshotExtension implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        Object testInstance = context.getRequiredTestInstance();
        try {
            Field driverField = testInstance.getClass().getDeclaredField("driver");
            driverField.setAccessible(true);
            WebDriver driver = (WebDriver) driverField.get(testInstance);

            if (driver != null) {
                String testName = context.getDisplayName().replaceAll("[^a-zA-Z0-9]", "_");
                FailureScreenshotListener.captureScreenshot(driver, testName);
            }
        } catch (Exception e) {
            System.err.println("Could not capture failure screenshot: " + e.getMessage());
        }

        // Re-throw exception so test failure is registered properly
        throw throwable;
    }
}
