package cc.wdev.platform.system.message.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.message.domain.entity.NoticeEntity;
import cc.wdev.platform.system.message.repository.NoticeRepository;
import cc.wdev.platform.system.message.request.NoticeSearchRequest;
import cc.wdev.platform.system.message.service.NoticeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;

/**
 * @author elvea
 */
@Slf4j
@AllArgsConstructor
@Service
public class NoticeServiceImpl extends BaseEntityService<NoticeEntity, Long, NoticeRepository> implements NoticeService {

    /**
     * @see NoticeService#findMyNoticeByUserId(NoticeSearchRequest)
     */
    @Override
    public Page<NoticeEntity> findMyNoticeByUserId(NoticeSearchRequest request) {
        request.setUserId(SecurityUtils.getUid());
        IPage<NoticeEntity> page = this.lambdaQueryWrapper()
            .eq(NoticeEntity::getRecipientId, request.getUserId())
            .eq(NoticeEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByAsc(NoticeEntity::getReadInd)
            .orderByDesc(NoticeEntity::getId)
            .page(getMyBatisPlusPage(request.getPageable()));
        return MyBatisPlusUtils.toSpringDataPage(page);
    }

    /**
     * 收件人查看时顺带标记已读；sys_notice.read_ind 只有这里会写，管理端不得代为修改他人已读状态
     */
    @Override
    public NoticeEntity findDetailsForRecipient(Long id) {
        NoticeEntity entity = this.findById(id);
        if (entity == null || !ActiveTypeEnum.ENABLED.getValue().equals(entity.getActive())
            || !Objects.equals(entity.getRecipientId(), SecurityUtils.getUid())) {
            return null;
        }
        if (!Objects.equals(entity.getReadInd(), 1)) {
            entity.setReadInd(1);
            entity.setReadDatetime(getCurLocalDateTime());
            this.save(entity);
        }
        return entity;
    }

    /**
     * 管理端公告分页列表
     */
    @Override
    public Page<NoticeEntity> findNoticeByPage(NoticeSearchRequest noticeSearchRequest) {
        String keyword = noticeSearchRequest.getQ();
        IPage<NoticeEntity> page = this.lambdaQueryWrapper()
            .and(StringUtils.isNotEmpty(keyword), wrapper -> {
                wrapper.like(NoticeEntity::getSubject, keyword);
            })
            .eq(NoticeEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .page(getMyBatisPlusPage(noticeSearchRequest.getPageable()));
        return MyBatisPlusUtils.toSpringDataPage(page);
    }

}
