package cc.wdev.platform.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/**
 * @author elvea
 */
@Slf4j
public class EnvironmentUtils implements EnvironmentAware {

    private static Environment environment;

    /**
     * @see EnvironmentAware#setEnvironment(Environment)
     */
    @Override
    public void setEnvironment(@NonNull Environment environment) {
        Assert.notNull(environment, "environment must not be null.");
        EnvironmentUtils.environment = environment;
    }

    /**
     * 获取配置项
     *
     * @param key 配置项
     * @return String
     */
    public String getProperty(String key) {
        return this.getProperty(key, "");
    }

    /**
     * 获取配置项
     *
     * @param key          配置项
     * @param defaultValue 默认值
     * @return String
     */
    public String getProperty(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    /**
     * 获取配置项
     *
     * @param key   配置项
     * @param clazz 返回值类型
     * @return T
     */
    public <T> T getProperty(String key, Class<T> clazz) {
        return environment.getProperty(key, clazz);
    }

    /**
     * 获取配置项
     *
     * @param key          配置项
     * @param clazz        返回值类型
     * @param defaultValue 默认值
     * @return T
     */
    public <T> T getProperty(String key, Class<T> clazz, T defaultValue) {
        return environment.getProperty(key, clazz, defaultValue);
    }

}
