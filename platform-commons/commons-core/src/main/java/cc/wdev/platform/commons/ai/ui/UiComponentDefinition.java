package cc.wdev.platform.commons.ai.ui;

import java.util.Map;

/**
 * @author elvea
 */
public interface UiComponentDefinition {

    String type();

    String description();

    Map<String, Object> propsSchema();

}
