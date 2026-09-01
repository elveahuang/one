package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.BaseTests;
import cc.wdev.platform.commons.ai.AiConstants;
import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.commons.ai.enums.AiChatType;
import cc.wdev.platform.commons.ai.utils.AiUtils;
import cc.wdev.platform.system.ai.domain.request.*;
import cc.wdev.platform.system.ai.domain.vo.AiAgentVo;
import cc.wdev.platform.system.ai.domain.vo.AiKbVo;
import cc.wdev.platform.system.ai.domain.vo.AiModelVo;
import cc.wdev.platform.system.ai.domain.vo.AiToolSimpleVo;
import cc.wdev.platform.system.ai.enums.AiAgentBizTypeEnum;
import cc.wdev.platform.system.ai.enums.AiKbBizTypeEnum;
import cc.wdev.platform.system.ai.enums.AiModelBizTypeEnum;
import cc.wdev.platform.system.commons.domain.request.GetRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springaicommunity.agent.tools.SkillsTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.List;

import static cc.wdev.platform.commons.ai.AiConstants.DEFAULT_SESSION_ID;
import static cc.wdev.platform.commons.ai.AiConstants.DEFAULT_TEST_VECTOR_STORE_COLLECTION_NAME;
import static cc.wdev.platform.commons.constants.SecurityConstants.ROOT_USER;

/**
 * @author elvea
 */
@Slf4j
public class AiBaseTests extends BaseTests {

    @Autowired
    private AiManager aiManager;

    @Autowired
    private AiAgentApi aiAgentApi;

    @Autowired
    private AiModelApi aiModelApi;

    @Autowired
    private AiKbApi aiKbApi;

    @Autowired
    private AiToolApi aiToolApi;

    @Autowired
    private AiChatApi aiChatApi;

    /**
     * 初始化基础数据
     */
    @Test
    public void initialize() {
        aiToolApi.initialize();
        aiModelApi.initialize();
        aiAgentApi.initialize();
        aiKbApi.initialize();
    }

    /**
     * 初始化系统默认智能体
     */
    @Test
    public void initializeAgent() {
        // 初始化文本模型
        AiModelVo chatModelVo = aiModelApi.getAiModel(AiModelGetRequest.builder().code(AiModelBizTypeEnum.DEEPSEEK_V4_FLASH.getValue()).build());
        Assertions.assertNotNull(chatModelVo);

        AiModelSaveRequest chatModelSaveRequest = AiModelSaveRequest.builder()
            .id(chatModelVo.getId())
            .code(chatModelVo.getCode())
            .title(chatModelVo.getTitle())
            .apiKey(System.getenv("DEEPSEEK_API_KEY"))
            .build();
        this.aiModelApi.saveAiModel(chatModelSaveRequest);

        // 初始化向量模型
        AiModelVo embeddingModelVo = aiModelApi.getAiModel(AiModelGetRequest.builder().code(AiModelBizTypeEnum.ALIYUN_TEXT_EMBEDDING.getValue()).build());
        Assertions.assertNotNull(embeddingModelVo);

        AiModelSaveRequest embeddingModelSaveRequest = AiModelSaveRequest.builder()
            .id(embeddingModelVo.getId())
            .code(embeddingModelVo.getCode())
            .title(embeddingModelVo.getTitle())
            .apiKey(System.getenv("DASHSCOPE_API_KEY"))
            .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
            .build();
        this.aiModelApi.saveAiModel(embeddingModelSaveRequest);

        // 初始化知识库
        AiKbVo aiKbVo = aiKbApi.getKb(GetRequest.builder().code(AiKbBizTypeEnum.TEST.getValue()).build());
        Assertions.assertNotNull(aiKbVo);

        AiKbSaveRequest aiKbSaveRequest = AiKbSaveRequest.builder()
            .id(aiKbVo.getId())
            .code(aiKbVo.getCode())
            .title(aiKbVo.getTitle())
            .embeddingModelId(embeddingModelVo.getId())
            .chatModelId(chatModelVo.getId())
            .collectionName(DEFAULT_TEST_VECTOR_STORE_COLLECTION_NAME)
            .build();
        aiKbApi.saveKb(aiKbSaveRequest);

        // 读取现有工具
        List<AiToolSimpleVo> tools = this.aiToolApi.getTools();

        // 读取默认智能体
        AiAgentVo aiAgentVo = this.aiAgentApi.getAiAgent(AiAgentGetRequest.builder().code(AiAgentBizTypeEnum.TEST.getValue()).build());
        Assertions.assertNotNull(aiAgentVo);

        // 保存默认智能体
        AiAgentSaveRequest aiAgentSaveRequest = AiAgentSaveRequest.builder()
            .id(aiAgentVo.getId())
            .code(aiAgentVo.getCode())
            .title(aiAgentVo.getTitle())
            .modelId(chatModelVo.getId())
            .kbId(aiKbVo.getId())
            .toolIds(tools.stream().map(AiToolSimpleVo::getId).toList())
            .systemPrompt(AiConstants.DEFDAULT_PROMPT)
            .build();
        this.aiAgentApi.saveAiAgent(aiAgentSaveRequest);
    }

    @Test
    @WithUserDetails(value = ROOT_USER)
    public void simpleTest() {
        SimpleChatRequest request = SimpleChatRequest.builder()
            .chatType(AiChatType.AGENT.getValue())
            .agentCode(AiAgentBizTypeEnum.TEST.getValue())
            .prompt("教师节是几月几号")
            .conversationId(DEFAULT_SESSION_ID)
            .build();
        String text = aiChatApi.chatText(request);
        Assertions.assertNotNull(text);
    }

    @Test
    @WithUserDetails(value = ROOT_USER)
    public void chatTextTest() {
        SimpleChatRequest request = SimpleChatRequest.builder()
            .chatType(AiChatType.AGENT.getValue())
            .agentCode(AiAgentBizTypeEnum.TEST.getValue())
            .prompt("获取应用版本号，并且获取系统当前时间，并看看教师节是什么日期")
            .conversationId(DEFAULT_SESSION_ID)
            .build();
        String text = aiChatApi.chatText(request);
        Assertions.assertNotNull(text);

        request = SimpleChatRequest.builder()
            .chatType(AiChatType.AGENT.getValue())
            .agentCode(AiAgentBizTypeEnum.TEST.getValue())
            .prompt("教师节")
            .conversationId(DEFAULT_SESSION_ID)
            .build();
        text = aiChatApi.chatText(request);
        Assertions.assertNotNull(text);
    }

    /**
     * 技能测试
     */
    @Test
    @WithUserDetails(value = ROOT_USER)
    public void skillTest() {
        ToolCallback toolCallback = SkillsTool.builder()
            .addSkillsResource(new ClassPathResource("META-INF/cc.wdev/skills"))
            .build();

        ChatModel chatModel = this.aiManager.getChatModel();
        ChatClient chatClient = ChatClient.builder(chatModel)
            .defaultTools(toolCallback)
            .build();

        ChatResponse response = chatClient
            .prompt()
            .user("你好")
            .call()
            .chatResponse();
        Assertions.assertNotNull(response);

        String text = AiUtils.getChatResponseContent(response);
        Assertions.assertNotNull(text);
    }

}
