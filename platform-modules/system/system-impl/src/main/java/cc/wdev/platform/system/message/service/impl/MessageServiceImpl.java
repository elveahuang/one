package cc.wdev.platform.system.message.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.web.request.PageRequest;
import cc.wdev.platform.system.message.domain.entity.MessageEntity;
import cc.wdev.platform.system.message.enums.MessageStatusEnum;
import cc.wdev.platform.system.message.repository.MessageRepository;
import cc.wdev.platform.system.message.service.MessageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.toSpringDataPage;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class MessageServiceImpl
    extends BaseEntityService<MessageEntity, Long, MessageRepository>
    implements MessageService {

    /**
     * @see MessageService#findByStatus(List)
     */
    @Override
    public List<MessageEntity> findByStatus(final List<MessageStatusEnum> statusList) {
        List<Integer> statusValueList = statusList.stream().map(MessageStatusEnum::getValue).toList();
        return lambdaQueryWrapper()
            .eq(MessageEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .in(MessageEntity::getStatus, statusValueList)
            .list();
    }

    /**
     * 获取消息列表
     */
    @Override
    public Page<MessageEntity> findMessageList(PageRequest request) {
        IPage<MessageEntity> page = this.lambdaQueryWrapper()
            .eq(MessageEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .like(StringUtils.isNotEmpty(request.getQ()), MessageEntity::getSubject, request.getQ())
            .page(getMyBatisPlusPage(request.getPageable()));
        return toSpringDataPage(page);
    }

}
