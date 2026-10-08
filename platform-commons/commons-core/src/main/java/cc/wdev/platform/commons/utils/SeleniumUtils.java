package cc.wdev.platform.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.util.Map;

@Slf4j
public abstract class SeleniumUtils {

    public static <T extends ChromiumOptions<T>> T withDefaultOptions(T options) {
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("start-maximized");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        return options;
    }

    public static <T extends ChromiumOptions<T>> T withDefaultMobileOptions(T options) {
        options.setExperimentalOption("mobileEmulation", Map.of("deviceName", "iPhone X"));
        return options;
    }

    public static ChromeOptions getDefaultChromeOptions() {
        return withDefaultOptions(new ChromeOptions());
    }

    public static ChromeOptions getDefaultMobileChromeOptions() {
        return withDefaultMobileOptions(getDefaultChromeOptions());
    }

    public static EdgeOptions getDefaultEdgeOptions() {
        return withDefaultOptions(new EdgeOptions());
    }

    public static EdgeOptions getDefaultMobileEdgeOptions() {
        return withDefaultMobileOptions(getDefaultEdgeOptions());
    }

    public static ChromeDriver getChromeDriver() {
        return getChromeDriver(getDefaultChromeOptions());
    }

    public static ChromeDriver getChromeDriver(ChromeOptions options) {
        return new ChromeDriver(options);
    }

    public static EdgeDriver getEdgeDriver() {
        return getEdgeDriver(getDefaultEdgeOptions());
    }

    public static EdgeDriver getEdgeDriver(EdgeOptions options) {
        return new EdgeDriver(options);
    }

    public static void closeWebDriver(WebDriver driver) {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                log.error("Fail to close WebDriver", e);
            }
        }
    }

}
