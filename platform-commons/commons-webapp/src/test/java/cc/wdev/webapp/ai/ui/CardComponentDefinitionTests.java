package cc.wdev.webapp.ai.ui;

import cc.wdev.platform.commons.ai.ui.UiComponentRegistry;
import cc.wdev.platform.commons.utils.GsonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 卡片组件 Schema 的单元测试
 *
 * @author elvea
 */
public class CardComponentDefinitionTests {

    private final UiComponentRegistry registry = new UiComponentRegistry();

    @BeforeEach
    public void setUp() {
        this.registry.register(new CourseListViewComponentDefinition());
        this.registry.register(new InstructorListViewComponentDefinition());
    }

    /**
     * 列表类组件统一为 items 数组，条目字段在 items.items.properties 下
     */
    @Test
    public void courseListViewSchemaTest() {
        Map<String, Object> props = this.propsSchemaOf("course-list-view");

        Assertions.assertEquals(List.of("items"), props.get("required"));
        Assertions.assertEquals(Boolean.FALSE, props.get("additionalProperties"));

        Map<String, Object> items = map(map(props.get("properties")).get("items"));
        Assertions.assertEquals("array", items.get("type"));

        Map<String, Object> itemSchema = map(items.get("items"));
        Map<String, Object> itemProperties = map(itemSchema.get("properties"));
        Assertions.assertTrue(itemProperties.containsKey("courseId"), itemProperties.toString());
        Assertions.assertTrue(itemProperties.containsKey("courseTitle"), itemProperties.toString());
        Assertions.assertEquals(List.of("courseId", "courseTitle"), itemSchema.get("required"));
        Assertions.assertEquals(Boolean.FALSE, itemSchema.get("additionalProperties"));
    }

    @Test
    public void instructorListViewSchemaTest() {
        Map<String, Object> props = this.propsSchemaOf("instructor-list-view");

        Map<String, Object> items = map(map(props.get("properties")).get("items"));
        Map<String, Object> itemProperties = map(map(items.get("items")).get("properties"));
        Assertions.assertTrue(itemProperties.containsKey("instructorId"), itemProperties.toString());
        Assertions.assertTrue(itemProperties.containsKey("instructorName"), itemProperties.toString());
    }

    /**
     * props schema 必须内联生成：出现 $ref/$defs 时，嵌入 oneOf 后引用会指向错误位置
     */
    @Test
    public void propsSchemaShouldNotUseRef() {
        String schema = this.registry.buildJsonSchema();
        Map<String, Object> props = this.propsSchemaOf("course-list-view");

        Assertions.assertFalse(schema.contains("$ref"), schema);
        Assertions.assertFalse(schema.contains("$defs"), schema);
        Assertions.assertFalse(props.containsKey("$schema"), props.toString());
    }

    /**
     * 响应信封与根结构同样由 POJO 生成，组件差异只在 type 与 props
     */
    @Test
    public void responseEnvelopeSchemaTest() {
        Map<String, Object> schema = GsonUtils.toObjectMap(this.registry.buildJsonSchema());

        Assertions.assertEquals("object", schema.get("type"));
        Assertions.assertEquals(List.of("blocks"), schema.get("required"));
        Assertions.assertEquals(Boolean.FALSE, schema.get("additionalProperties"));
        Assertions.assertNotNull(schema.get("$schema"));

        Map<String, Object> blocks = map(map(schema.get("properties")).get("blocks"));
        Assertions.assertEquals("array", blocks.get("type"));

        // items 只承载 oneOf 变体，不重复通用信封
        Map<String, Object> items = map(blocks.get("items"));
        Assertions.assertEquals(Set.of("oneOf"), items.keySet());

        List<Map<String, Object>> variants = list(items.get("oneOf"));
        Assertions.assertEquals(2, variants.size());

        Map<String, Object> variant = variants.get(0);
        Assertions.assertEquals(List.of("id", "props", "type"), variant.get("required"));
        Assertions.assertEquals(Boolean.FALSE, variant.get("additionalProperties"));

        Map<String, Object> properties = map(variant.get("properties"));
        Assertions.assertEquals("string", map(properties.get("id")).get("type"));
        Assertions.assertEquals("course-list-view", map(properties.get("type")).get("const"));
    }

    private Map<String, Object> propsSchemaOf(String type) {
        Map<String, Object> schema = GsonUtils.toObjectMap(this.registry.buildJsonSchema());
        Map<String, Object> blocks = map(map(schema.get("properties")).get("blocks"));
        List<Map<String, Object>> variants = list(map(blocks.get("items")).get("oneOf"));

        return variants.stream()
            .filter(variant -> type.equals(map(map(variant.get("properties")).get("type")).get("const")))
            .findFirst()
            .map(variant -> map(map(variant.get("properties")).get("props")))
            .orElseThrow(() -> new AssertionError("Schema variant not found: " + type));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> map(Object value) {
        return (Map<String, Object>) value;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> list(Object value) {
        return (List<Map<String, Object>>) value;
    }

}
