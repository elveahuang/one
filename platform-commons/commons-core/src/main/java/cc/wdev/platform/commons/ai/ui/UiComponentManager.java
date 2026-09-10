package cc.wdev.platform.commons.ai.ui;

/**
 * @author elvea
 */
public class UiComponentManager {

    private UiComponentManager() {
    }

    private static volatile UiComponentRegistry globalUiComponentRegistry = new UiComponentRegistry();

    public static UiComponentRegistry getRegistry() {
        return globalUiComponentRegistry;
    }

    public static void setRegistry(UiComponentRegistry registry) {
        globalUiComponentRegistry = registry;
    }

}
