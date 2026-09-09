package cc.wdev.platform.commons.ai.ui;

import tools.jackson.databind.node.ObjectNode;

/**
 * @author elvea
 */
public interface UiComponentDefinition {

    String type();

    String description();

    ObjectNode propsSchema();

}
