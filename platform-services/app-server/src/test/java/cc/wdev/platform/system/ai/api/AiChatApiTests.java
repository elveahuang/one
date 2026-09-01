package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.BaseTests;
import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.commons.ai.enums.AiChatType;
import cc.wdev.platform.system.ai.enums.AiToolBizTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author elvea
 */
@Slf4j
public class AiChatApiTests extends BaseTests {

    @Autowired
    private AiManager aiManager;

    @Autowired
    private AiChatApi aiChatApi;

    @Test
    public void baseTest() {
        Assertions.assertNotNull(this.aiChatApi);
    }

    @Test
    public void baseStaticChatTest() {
        SimpleChatRequest request = SimpleChatRequest.builder()
            .chatType(AiChatType.STATIC.getValue())
            .prompt("你好")
            .systemPrompt("你是一个智能助手，当用户说你好的时候，你就回答天气很好")
            .build();
        String text = this.aiChatApi.chatText(request);
        Assertions.assertNotNull(text);
    }

    @Test
    public void baseStaticToolCallingTest() {
        SimpleChatRequest request = SimpleChatRequest.builder()
            .chatType(AiChatType.STATIC.getValue())
            .withToolCalling(true)
            .toolNames(Lists.newArrayList(AiToolBizTypeEnum.GET_APPLICATION_VERSION.getToolName()))
            .prompt("获取应用版本号")
            .build();
        String text = this.aiChatApi.chatText(request);
        Assertions.assertNotNull(text);
    }

    @Test
    public void baseStaticRagChatTest() {
        // 保存知识库文档
        VectorStore store = this.aiManager.getVectorStore();
        store.add(List.of(
            new Document("1", "教师节是每年的9月11号", Maps.newHashMap()),
            new Document("2", "劳动节是每年的5月1号", Maps.newHashMap()),
            new Document("3", "植树节是每年的3月12号", Maps.newHashMap())
        ));

        // 发起对话
        SimpleChatRequest request = SimpleChatRequest.builder()
            .chatType(AiChatType.STATIC.getValue())
            .withRag(true)
            .withSession(true)
            .withToolCalling(true)
            .prompt("教师节")
            .build();
        String text = this.aiChatApi.chatText(request);
        Assertions.assertNotNull(text);
    }

}
