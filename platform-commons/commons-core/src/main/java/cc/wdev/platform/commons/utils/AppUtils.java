package cc.wdev.platform.commons.utils;

/**
 * @author elvea
 */
public abstract class AppUtils {

    public static void init() {
        System.setProperty("org.jboss.logging.provider", "slf4j");
    }

}
