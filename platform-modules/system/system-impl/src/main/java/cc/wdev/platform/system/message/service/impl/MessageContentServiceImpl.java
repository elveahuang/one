package cc.wdev.platform.system.message.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.system.message.domain.entity.MessageContentEntity;
import cc.wdev.platform.system.message.enums.MessageStatusEnum;
import cc.wdev.platform.system.message.repository.MessageContentRepository;
import cc.wdev.platform.system.message.service.MessageContentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
@Service
public class MessageContentServiceImpl
    extends BaseEntityService<MessageContentEntity, Long, MessageContentRepository>
    implements MessageContentService {

    /**
     * @see MessageContentService#findByMessage(Long)
     */
    @Override
    public List<MessageContentEntity> findByMessage(Long messageId) {
        return lambdaQueryWrapper()
            .eq(MessageContentEntity::getMessageId, messageId)
            .list();
    }

    /**
     * @see MessageContentService#success(Long, String)
     */
    @Override
    public void success(Long id, String resp) {
        MessageContentEntity entity = this.findById(id);
        if (entity != null) {
            entity.setResp(resp);
            entity.setSentDatetime(getCurLocalDateTime());
            entity.setStatus(MessageStatusEnum.SENT.getValue());
            this.save(entity);
        }
    }

    /**
     * @see MessageContentService#fail(Long, String)
     */
    @Override
    public void fail(Long id, String resp) {
        this.fail(id, resp, "");
    }

    /**
     * @see MessageContentService#fail(Long, String, String)
     */
    @Override
    public void fail(Long id, String resp, String exception) {
        MessageContentEntity entity = this.findById(id);
        if (entity != null) {
            entity.setResp(resp);
            entity.setException(exception);
            entity.setSentDatetime(getCurLocalDateTime());
            entity.setStatus(MessageStatusEnum.FAIL.getValue());
            this.save(entity);
        }
    }

}
