package cc.wdev.platform.commons.ai.ui;

import java.util.Map;

/**
 * @author elvea
 */
public record UiBlock(String id, String type, Map<String, Object> props) {
}
