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
            .orderByAsc(NoticeEntity::getReadInd)
            .page(getMyBatisPlusPage(request.getPageable()));
        return MyBatisPlusUtils.toSpringDataPage(page);
    }

    /**
     * 根据用户id查询消息列表
     * 根据标题关键词查询消息列表
     */
    @Override
    public Page<NoticeEntity> findNoticeByPage(NoticeSearchRequest noticeSearchRequest) {
        Long userId = SecurityUtils.getUid();
        IPage<NoticeEntity> page;
        String keyword;
        try {
            keyword = noticeSearchRequest.getQ();
            page = this.lambdaQueryWrapper()
                .and(StringUtils.isNotEmpty(keyword), wrapper -> {
                    wrapper.like(NoticeEntity::getSubject, keyword);
                })
                .eq(NoticeEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
                .page(getMyBatisPlusPage(noticeSearchRequest.getPageable()));
        } catch (Exception e) {
            page = this.lambdaQueryWrapper()
                .eq(NoticeEntity::getRecipientId, userId)
                .eq(NoticeEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
                .orderByAsc(NoticeEntity::getReadInd)
                .page(getMyBatisPlusPage(noticeSearchRequest.getPageable()));
        }
        return MyBatisPlusUtils.toSpringDataPage(page);
    }

}
