package cc.wdev.platform.commons.core.log.config;

/**
 * @author elvea
 */
@FunctionalInterface
public interface LogConfigCustomizer {

    void customize(LogConfig config);

}
