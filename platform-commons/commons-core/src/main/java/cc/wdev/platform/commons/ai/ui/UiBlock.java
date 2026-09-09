package cc.wdev.platform.commons.ai.ui;

import tools.jackson.databind.JsonNode;

/**
 * @author elvea
 */
public record UiBlock(String id, String type, JsonNode props) {
}
