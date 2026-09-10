package cc.wdev.platform.commons.ai.ui;

/**
 * @author elvea
 */
public class UiComponentManager {

    private static volatile UiComponentRegistry gloablUiComponentRegistry = new UiComponentRegistry();

    public static UiComponentRegistry getRegistry() {
        return gloablUiComponentRegistry;
    }

    public static void setRegistry(UiComponentRegistry registry) {
        gloablUiComponentRegistry = registry;
    }

}
