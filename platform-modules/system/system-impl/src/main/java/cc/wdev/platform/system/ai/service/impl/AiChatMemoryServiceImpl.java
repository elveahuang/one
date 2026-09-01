package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.ai.enums.AiChatType;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.entity.AiChatMemoryEntity;
import cc.wdev.platform.system.ai.domain.request.AiChatDeleteRequest;
import cc.wdev.platform.system.ai.domain.request.AiChatGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiChatSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiChatVo;
import cc.wdev.platform.system.ai.repository.AiChatMemoryRepository;
import cc.wdev.platform.system.ai.service.AiChatMemoryService;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;

/**
 * @author elvea
 */
@Slf4j
@Service
public class AiChatMemoryServiceImpl
    extends BaseCachingEntityService<AiChatMemoryEntity, Long, AiChatMemoryRepository>
    implements AiChatMemoryService {

    /**
     * @see AiChatMemoryService#findConversationIds()
     */
    @Override
    public List<String> findConversationIds() {
        return this.lambdaQueryWrapper()
            .select(AiChatMemoryEntity::getConversationId)
            .eq(AiChatMemoryEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list()
            .stream()
            .map(AiChatMemoryEntity::getConversationId)
            .toList();
    }

    /**
     * @see AiChatMemoryService#findConversationIds()
     */
    @Override
    public List<AiChatMemoryEntity> findByConversationId(String conversationId, int limit) {
        if (StringUtils.isEmpty(conversationId)) {
            return Collections.emptyList();
        }
        // 记忆窗口语义：取"最近" limit 条，按创建时间倒序取第一页后再反转为升序
        List<AiChatMemoryEntity> list = this.lambdaQueryWrapper()
            .eq(AiChatMemoryEntity::getConversationId, conversationId)
            .eq(AiChatMemoryEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByDesc(AiChatMemoryEntity::getCreatedAt)
            .orderByDesc(AiChatMemoryEntity::getId)
            .list(MyBatisPlusUtils.getLimitPage(limit));
        Collections.reverse(list);
        return list;
    }

    /**
     * @see AiChatMemoryService#deleteByConversationId(String)
     */
    @Override
    public void deleteByConversationId(String conversationId) {
        if (StringUtils.isNotEmpty(conversationId)) {
            this.lambdaUpdateWrapper()
                .eq(AiChatMemoryEntity::getConversationId, conversationId)
                .set(AiChatMemoryEntity::getActive, ActiveTypeEnum.DISABLED.getValue())
                .update();
        }
    }

    @Override
    public AiChatVo getChat(AiChatGetRequest request) {
        if (StringUtils.isEmpty(request.getConversationId())) {
            return null;
        }

        List<AiChatMemoryEntity> list = this.lambdaQueryWrapper()
            .eq(AiChatMemoryEntity::getTenantId, ObjectUtils.isEmpty(request.getTenantId()) ? TenantContext.getTenantId() : request.getTenantId())
            .eq(StringUtils.isNotEmpty(request.getChatType()), AiChatMemoryEntity::getChatType, request.getChatType())
            .eq(AiChatMemoryEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(AiChatMemoryEntity::getConversationId, request.getConversationId())
            .eq(AiChatMemoryEntity::getUserId, SecurityUtils.getUid())
            .orderByAsc(AiChatMemoryEntity::getCreatedAt)
            .list();

        if (CollectionUtils.isEmpty(list)) {
            return AiChatVo.builder().conversationId(request.getConversationId()).messages(Collections.emptyList()).build();
        }

        // 转换实体列表为Message对象列表
        List<Message> messages = list.stream()
            .map(entity -> {
                String content = entity.getContent();
                String type = entity.getType();

                // 根据type字段创建对应的Message类型
                return switch (type) {
                    case "assistant" -> new AssistantMessage(content);
                    case "system" -> new SystemMessage(content);
                    default -> new UserMessage(content);
                };
            })
            .collect(Collectors.toList());

        // 生成对话标题（使用第一条消息的前50个字符）
        String title = list.isEmpty() ? "" :
            StrUtil.subPre(list.getFirst().getContent(), 50);

        return AiChatVo.builder()
            .conversationId(request.getConversationId())
            .title(title)
            .messages(messages)
            .build();
    }

    @Override
    public void deleteChat(AiChatDeleteRequest request) {
        if (ObjectUtils.isEmpty(request.getIds())) {
            return;
        }

        this.lambdaUpdateWrapper()
            .eq(AiChatMemoryEntity::getTenantId, ObjectUtils.isEmpty(request.getTenantId()) ? TenantContext.getTenantId() : request.getTenantId())
            .in(AiChatMemoryEntity::getConversationId, CollectionUtils.arrayToList(request.getIds()))
            .eq(AiChatMemoryEntity::getUserId, SecurityUtils.getUid())
            .set(AiChatMemoryEntity::getActive, ActiveTypeEnum.DISABLED.getValue())
            .set(AiChatMemoryEntity::getDeletedAt, LocalDateTime.now())
            .set(AiChatMemoryEntity::getDeletedBy, SecurityUtils.getUid())
            .update();
    }

    @Override
    public Page<AiChatVo> findMyChats(AiChatSearchRequest request) {
        if (ObjectUtils.isEmpty(request.getTenantId())) {
            request.setTenantId(TenantContext.getTenantId());
        }
        // 用户ID恒以当前登录用户为准，禁止客户端传参覆盖
        request.setUserId(SecurityUtils.getUid());
        if (StringUtils.isEmpty(request.getChatType())) {
            request.setChatType(AiChatType.CHAT.getValue());
        }

        // 使用窗口函数获取所有符合条件的对话记录
        IPage<AiChatVo> page = this.mapper.findAllChatsWithWindowFunction(getMyBatisPlusPage(request.getPageable()), request);
        return MyBatisPlusUtils.toSpringDataPage(request.getPageable(), page.getRecords(), page.getTotal());
    }

}
