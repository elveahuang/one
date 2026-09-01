package cc.wdev.platform.commons.extensions.selenium;

import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.chromium.ChromiumOptions;

/**
 * @author elvea
 */
public interface SeleniumFactory {

    /**
     * @return {@see SeleniumConfig}
     */
    SeleniumConfig config();

    /**
     * 初始化
     */
    void initialize();

    /**
     * @return {@see ChromiumOptions}
     */
    ChromiumOptions<?> getDefaultOptions();

    /**
     * @return {@see ChromiumOptions}
     */
    ChromiumOptions<?> getDefaultMobileOptions();

    /**
     * @return {@see ChromiumDriver}
     */
    ChromiumDriver getWebDriver();

    /**
     * @return {@see ChromiumDriver}
     */
    ChromiumDriver getWebDriver(ChromiumOptions<?> options);

}
