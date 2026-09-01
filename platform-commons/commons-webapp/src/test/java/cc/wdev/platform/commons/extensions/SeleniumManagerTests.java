package cc.wdev.platform.commons.extensions;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.extensions.selenium.SeleniumFactory;
import cc.wdev.platform.commons.utils.SeleniumUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
@Slf4j
public class SeleniumManagerTests extends BaseTests {

    @Autowired
    private SeleniumFactory seleniumFactory;

    public void baseTest(WebDriver driver) throws Exception {
        log.info("Open page [{}]. ", "https://qq.com");
        driver.get("https://qq.com");
        Thread.sleep(3000);
        String title = driver.getTitle();
        Assertions.assertNotNull(title);
    }

    @Test
    public void baseWebTest() throws Exception {
        WebDriver driver = null;
        try {
            driver = this.seleniumFactory.getWebDriver();
            baseTest(driver);
        } finally {
            SeleniumUtils.closeWebDriver(driver);
        }
    }

    @Test
    public void baseDesktopTest() throws Exception {
        WebDriver driver = null;
        try {
            driver = this.seleniumFactory.getWebDriver(this.seleniumFactory.getDefaultOptions());
            baseTest(driver);
        } finally {
            SeleniumUtils.closeWebDriver(driver);
        }
    }

    @Test
    public void baseMobileTest() throws Exception {
        WebDriver driver = null;
        try {
            driver = this.seleniumFactory.getWebDriver(this.seleniumFactory.getDefaultMobileOptions());
            baseTest(driver);
        } finally {
            SeleniumUtils.closeWebDriver(driver);
        }
    }

}
