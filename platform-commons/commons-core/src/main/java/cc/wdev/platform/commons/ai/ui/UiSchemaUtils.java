package cc.wdev.platform.commons.ai.ui;

import cc.wdev.platform.commons.utils.JacksonUtils;
import org.springframework.ai.util.json.schema.JsonSchemaGenerator;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * UI 组件 props 的 JSON Schema 生成工具。
 * <p>
 * 由 POJO 自动生成，避免手写嵌套的 {@code Map<String, Object>}：
 * 字段默认必填（用 {@code @ToolParam(required = false)} 或 {@code @Nullable} 标注可选），
 * 描述用 {@code @Schema} / {@code @JsonPropertyDescription} 标注。
 *
 * @author elvea
 */
public abstract class UiSchemaUtils {

    private static final String SCHEMA_KEY = "$schema";

    public static Map<String, Object> of(Type type) {
        Map<String, Object> schema = JacksonUtils.toMap(JsonSchemaGenerator.generateForType(type));
        schema.remove(SCHEMA_KEY);
        return schema;
    }

}
