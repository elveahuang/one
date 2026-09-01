package cc.wdev.platform.commons.extensions.selenium;

import cc.wdev.platform.commons.enums.SeleniumDriverTypeEnum;
import cc.wdev.platform.commons.utils.SeleniumUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.chromium.ChromiumOptions;
import org.openqa.selenium.edge.EdgeOptions;

/**
 * @author elvea
 */
@Slf4j
public record DefaultSeleniumFactory(SeleniumConfig config) implements SeleniumFactory {
    /**
     * @see SeleniumFactory#initialize()
     */
    @Override
    public void initialize() {
        WebDriverManager webDriverManager = SeleniumDriverTypeEnum.CHROME.equals(config.getDriverType()) ?
            WebDriverManager.chromedriver() : WebDriverManager.edgedriver();
        log.info("WebDriverManager {} setup", config.getDriverType());
        if (config.getProxy().isEnabled()) {
            String host = config.getProxy().getHost();
            Integer port = config.getProxy().getPort();
            log.info("WebDriverManager {} - proxy enabled", config.getDriverType());
            log.info("WebDriverManager {} - proxy [{}][{}]", config.getDriverType(), host, port);

            webDriverManager.proxy("%s:%s".formatted(host, port));
        } else {
            log.info("WebDriverManager {} - proxy disabled", config.getDriverType());
        }
        if (StringUtils.isNotEmpty(config.getDriverVersion())) {
            webDriverManager.driverVersion(config.getDriverVersion());

            log.info("WebDriverManager {} - version [{}]", config.getDriverType(), config.getDriverVersion());
        } else {
            log.info("WebDriverManager {} - version latest", config.getDriverType());
        }
        try {
            webDriverManager.setup();
            log.info("WebDriverManager {} setup done", config.getDriverType());
        } catch (Exception e) {
            log.error("WebDriverManager {} - setup failed", config.getDriverType(), e);
        }
    }

    /**
     * @see SeleniumFactory#getDefaultOptions()
     */
    @Override
    public ChromiumOptions<?> getDefaultOptions() {
        if (SeleniumDriverTypeEnum.EDGE.equals(config.getDriverType())) {
            return SeleniumUtils.getDefaultEdgeOptions();
        }
        return SeleniumUtils.getDefaultChromeOptions();
    }

    /**
     * @see SeleniumFactory#getDefaultMobileOptions()
     */
    @Override
    public ChromiumOptions<?> getDefaultMobileOptions() {
        if (SeleniumDriverTypeEnum.EDGE.equals(config.getDriverType())) {
            return SeleniumUtils.getDefaultMobileEdgeOptions();
        }
        return SeleniumUtils.getDefaultMobileChromeOptions();
    }

    /**
     * @see SeleniumFactory#getWebDriver()
     */
    @Override
    public ChromiumDriver getWebDriver() {
        if (SeleniumDriverTypeEnum.EDGE.equals(config.getDriverType())) {
            return SeleniumUtils.getEdgeDriver();
        }
        return SeleniumUtils.getChromeDriver();
    }

    /**
     * @see SeleniumFactory#getWebDriver(ChromiumOptions)
     */
    @Override
    public ChromiumDriver getWebDriver(ChromiumOptions<?> driverOptions) {
        if (driverOptions instanceof EdgeOptions options) {
            return SeleniumUtils.getEdgeDriver(options);
        } else if (driverOptions instanceof ChromeOptions options) {
            return SeleniumUtils.getChromeDriver(options);
        }
        return SeleniumUtils.getChromeDriver();
    }

}
