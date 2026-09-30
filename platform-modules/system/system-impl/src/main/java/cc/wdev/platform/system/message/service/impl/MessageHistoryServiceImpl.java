package cc.wdev.platform.system.message.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.system.message.domain.entity.MessageHistoryEntity;
import cc.wdev.platform.system.message.repository.MessageHistoryRepository;
import cc.wdev.platform.system.message.service.MessageHistoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
@Service
public class MessageHistoryServiceImpl
    extends BaseEntityService<MessageHistoryEntity, Long, MessageHistoryRepository>
    implements MessageHistoryService {
}
