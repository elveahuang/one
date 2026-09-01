package cc.wdev.platform.system.ai.support;

import cc.wdev.platform.commons.ai.AiConstants;
import cc.wdev.platform.commons.ai.enums.AiChatType;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.entity.AiChatMemoryEntity;
import cc.wdev.platform.system.ai.service.AiChatMemoryService;
import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author elvea
 */
@Slf4j
@RequiredArgsConstructor
public class CustomChatMemoryRepository implements ChatMemoryRepository {

    private final AiChatMemoryService aiChatMemoryService;

    /**
     * @see ChatMemoryRepository#saveAll(String, List)
     */
    @Override
    @Transactional
    public void saveAll(@NonNull String conversationId, @NonNull List<Message> messages) {
        if (CollectionUtils.isEmpty(messages) || StringUtils.isEmpty(conversationId)) {
            return;
        }

        // 只保存新消息：历史消息通过 metadata 中的 chatMemoryId 识别并跳过
        // （findByConversationId 会为历史消息写入 chatMemoryId；新消息由
        //  CustomContextAdvisor 注入 userId/chatType/agentCode 等上下文）
        String chatType = AiChatType.CHAT.getValue();
        String agentCode = "";
        List<AiChatMemoryEntity> list = Lists.newArrayListWithExpectedSize(messages.size());
        for (Message message : messages) {
            chatType = MapUtils.getString(message.getMetadata(), AiConstants.METADATA_CHAT_TYPE, chatType);
            agentCode = MapUtils.getString(message.getMetadata(), AiConstants.METADATA_AGENT_CODE, agentCode);
            // 检查 metadata 中是否有数据库 ID
            // 如果有 ID，说明是历史消息，跳过不处理
            Long id = MapUtils.getLong(message.getMetadata(), AiConstants.METADATA_CHAT_MEMORY_ID, 0L);
            if (ObjectUtils.isValidId(id)) {
                continue;
            }

            // 新消息，构建实体准备插入
            AiChatMemoryEntity entity = new AiChatMemoryEntity();
            entity.setConversationId(conversationId);
            entity.setContent(message.getText());
            entity.setType(message.getMessageType().getValue());
            entity.setChatType(chatType);
            entity.setAgentCode(agentCode);

            // 直接从 message metadata 获取用户信息（由 UserContextChatMemoryAdvisor 注入）
            Long entityId = MapUtil.getLong(message.getMetadata(), AiConstants.METADATA_USER_ID, 0L);
            entity.setUserId(entityId);
            list.add(entity);
        }

        // 批量插入新消息
        if (CollectionUtils.isNotEmpty(list)) {
            aiChatMemoryService.saveBatch(list);
        }
    }

    /**
     * @see ChatMemoryRepository#deleteByConversationId(String)
     */
    @Override
    public void deleteByConversationId(@NonNull String conversationId) {
        if (StringUtils.isNotEmpty(conversationId)) {
            aiChatMemoryService.deleteByConversationId(conversationId);
        }
    }

    /**
     * @see ChatMemoryRepository#findConversationIds()
     */
    @NonNull
    @Override
    public List<String> findConversationIds() {
        List<String> conversationIds = aiChatMemoryService.findConversationIds();
        if (CollectionUtils.isEmpty(conversationIds)) {
            return Collections.emptyList();
        }
        return conversationIds;
    }

    /**
     * @see ChatMemoryRepository#findByConversationId(String)
     */
    @NonNull
    @Override
    public List<Message> findByConversationId(@NonNull String conversationId) {
        List<AiChatMemoryEntity> entityList = aiChatMemoryService.findByConversationId(conversationId);
        if (CollectionUtils.isEmpty(entityList)) {
            return List.of();
        }
        return entityList.stream().map((entity) -> {
            Map<String, Object> metadata = Maps.newHashMap();
            // 把数据库 ID 放入 metadata，这样 saveAll 时才能识别出是历史消息
            metadata.put(AiConstants.METADATA_CHAT_MEMORY_ID, entity.getId());
            metadata.put(AiConstants.METADATA_CHAT_TYPE, entity.getChatType());
            metadata.put(AiConstants.METADATA_USER_ID, entity.getUserId());
            metadata.put(AiConstants.METADATA_AGENT_CODE, entity.getAgentCode());

            String content = entity.getContent();
            MessageType type = MessageType.valueOf(entity.getType().toUpperCase());
            return switch (type) {
                case USER -> UserMessage.builder().text(content).metadata(metadata).build();
                case ASSISTANT -> AssistantMessage.builder().content(content).properties(metadata).build();
                case SYSTEM -> SystemMessage.builder().text(content).metadata(metadata).build();
                case TOOL -> ToolResponseMessage.builder().metadata(metadata).build();
            };
        }).collect(Collectors.toUnmodifiableList());
    }

}
