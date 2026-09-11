package cc.wdev.platform.commons.ai.utils;

import cc.wdev.platform.commons.utils.GsonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * json-render 围栏解析为卡片事件的单元测试
 *
 * @author elvea
 */
public class AiUtilsStreamTests {

    @Test
    public void plainTextStreamTest() {
        List<Map<String, Object>> parts = this.parts(Flux.just("你好", "，我是助手"));

        Assertions.assertEquals(1, parts.size());
        Assertions.assertEquals("text", parts.get(0).get("type"));
        Assertions.assertEquals("你好，我是助手", parts.get(0).get("content"));
    }

    /**
     * 围栏内容转成 block 事件，正文与卡片在同一个流里按顺序下发
     */
    @Test
    public void jsonRenderStreamTest() {
        List<Map<String, Object>> parts = this.parts(Flux.just(
            "已为你找到匹配课程。",
            "\n```json-render\n",
            "[{\"type\":\"course-list-view\",\"props\":{\"items\":["
                + "{\"courseId\":\"12\",\"courseTitle\":\"微服务架构实战\"},"
                + "{\"courseId\":\"13\",\"courseTitle\":\"分布式基础\"}]}}]",
            "\n```\n",
            "建议先补分布式基础。"));

        Assertions.assertEquals(3, parts.size());

        Assertions.assertEquals("text", parts.get(0).get("type"));
        Assertions.assertEquals("已为你找到匹配课程。", ((String) parts.get(0).get("content")).strip());

        Assertions.assertEquals("block", parts.get(1).get("type"));
        Map<String, Object> block = map(parts.get(1).get("block"));
        Assertions.assertEquals("course-list-view", block.get("type"));

        // items 必须保持数组形态，不能被字符串化
        List<Map<String, Object>> items = list(map(block.get("props")).get("items"));
        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals("12", items.get(0).get("courseId"));
        Assertions.assertEquals("微服务架构实战", items.get(0).get("courseTitle"));

        Assertions.assertEquals("text", parts.get(2).get("type"));
    }

    /**
     * 单个块对象（不带外层数组）同样支持
     */
    @Test
    public void singleBlockObjectStreamTest() {
        List<Map<String, Object>> parts = this.parts(Flux.just(
            "```json-render\n",
            "{\"type\":\"instructor-list-view\",\"props\":{\"items\":[{\"instructorId\":\"7\",\"instructorName\":\"李老师\"}]}}",
            "\n```"));

        Assertions.assertEquals(1, parts.size());
        Assertions.assertEquals("block", parts.get(0).get("type"));

        Map<String, Object> block = map(parts.get(0).get("block"));
        Assertions.assertEquals("instructor-list-view", block.get("type"));
        List<Map<String, Object>> items = list(map(block.get("props")).get("items"));
        Assertions.assertEquals("7", items.get(0).get("instructorId"));
        Assertions.assertEquals("李老师", items.get(0).get("instructorName"));
    }

    /**
     * 围栏内的多个块逐个下发，与 BLOCK 模式保持一致
     */
    @Test
    public void multiBlockStreamTest() {
        List<Map<String, Object>> parts = this.parts(Flux.just(
            "```json-render\n",
            "[{\"type\":\"course-list-view\",\"props\":{\"items\":[{\"courseId\":\"12\",\"courseTitle\":\"微服务架构实战\"}]}},"
                + "{\"type\":\"instructor-list-view\",\"props\":{\"items\":[{\"instructorId\":\"7\",\"instructorName\":\"李老师\"}]}}]",
            "\n```"));

        Assertions.assertEquals(2, parts.size());
        Assertions.assertEquals("course-list-view", map(parts.get(0).get("block")).get("type"));
        Assertions.assertEquals("instructor-list-view", map(parts.get(1).get("block")).get("type"));
    }

    /**
     * 围栏内容不是合法卡片时原样按文本下发，不丢失内容
     */
    @Test
    public void invalidJsonRenderStreamTest() {
        List<Map<String, Object>> parts = this.parts(Flux.just("```json-render\n", "{不是JSON}", "\n```"));

        Assertions.assertEquals(1, parts.size());
        Assertions.assertEquals("text", parts.get(0).get("type"));
        Assertions.assertTrue(((String) parts.get(0).get("content")).contains("json-render"));
    }

    private List<Map<String, Object>> parts(Flux<String> stream) {
        return AiUtils.processStream(stream)
            .map(part -> GsonUtils.toObjectMap(part))
            .collectList()
            .block();
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
