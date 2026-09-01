package cc.wdev.platform.system.site.service.impl;

import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.site.domain.converter.AnnouncementConverter;
import cc.wdev.platform.system.site.domain.entity.AnnouncementEntity;
import cc.wdev.platform.system.site.domain.form.AnnouncementForm;
import cc.wdev.platform.system.site.domain.request.AnnouncementSearchRequest;
import cc.wdev.platform.system.site.domain.vo.AnnouncementVo;
import cc.wdev.platform.system.site.repository.AnnouncementRepository;
import cc.wdev.platform.system.site.service.AnnouncementService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.toSpringDataPage;
import static cc.wdev.platform.commons.utils.DateTimeUtils.MAX_DATETIME;
import static cc.wdev.platform.commons.utils.DateTimeUtils.MIN_DATETIME;
import static cc.wdev.platform.commons.utils.ObjectUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
@Service
public class AnnouncementServiceImpl
    extends BaseCachingEntityService<AnnouncementEntity, Long, AnnouncementRepository> implements AnnouncementService {

    /**
     * @see AnnouncementService#findAnnouncementList(AnnouncementSearchRequest)
     */
    @Override
    public Page<AnnouncementVo> findAnnouncementList(AnnouncementSearchRequest request) {
        LocalDateTime now = getCurLocalDateTime();
        IPage<AnnouncementEntity> page = this.lambdaQueryWrapper()
            .eq(AnnouncementEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(AnnouncementEntity::getAllowCommentInd, BooleanTypeEnum.TRUE.getValue())
            .like(StringUtils.isNotBlank(request.getQ()), AnnouncementEntity::getTitle, request.getQ())
            .lt(AnnouncementEntity::getStartDatetime, now)
            .gt(AnnouncementEntity::getEndDatetime, now)
            .page(getMyBatisPlusPage(request.getPageable()));
        if (!MyBatisPlusUtils.isNotEmpty(page)) {
            return SpringDataUtils.emptyPage(request.getPageable());
        }

        List<AnnouncementVo> vos = page.getRecords().stream().map(AnnouncementConverter.INSTANCE::entity2Vo).toList();
        return toSpringDataPage(request.getPageable(), vos, page.getTotal());
    }

    /**
     * 根据标题关键字返回公告
     */
    @Override
    public Page<AnnouncementEntity> announcementsByKeyword(AnnouncementSearchRequest request) {
        IPage<AnnouncementEntity> page = this.lambdaQueryWrapper()
            .and(StringUtils.isNotBlank(request.getQ()), wrapper -> wrapper.like(AnnouncementEntity::getTitle, request.getQ()))
            .page(getMyBatisPlusPage(request.getPageable()));
        return MyBatisPlusUtils.toSpringDataPage(page);
    }

    /**
     * @see AnnouncementService#findPageAnnouncement(AnnouncementSearchRequest)
     */
    @Override
    public Page<AnnouncementVo> findPageAnnouncement(AnnouncementSearchRequest searchRequest) {
        IPage<AnnouncementEntity> page = lambdaQueryWrapper()
            .eq(AnnouncementEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(AnnouncementEntity::getAllowCommentInd, BooleanTypeEnum.getByValue(searchRequest.getAllowCommentInd()).getValue())
            .lt(AnnouncementEntity::getStartDatetime, getCurLocalDateTime())
            .gt(AnnouncementEntity::getEndDatetime, getCurLocalDateTime())
            .like(StringUtils.isNotBlank(searchRequest.getQ()), AnnouncementEntity::getTitle, searchRequest.getQ())
            .page(getMyBatisPlusPage(searchRequest.getPageable()));

        if (!MyBatisPlusUtils.isNotEmpty(page)) {
            return SpringDataUtils.emptyPage(searchRequest.getPageable());
        }

        List<AnnouncementVo> vos = page.getRecords().stream().map(AnnouncementConverter.INSTANCE::entity2Vo).toList();
        return toSpringDataPage(searchRequest.getPageable(), vos, page.getTotal());
    }

    @Override
    public AnnouncementVo getAnnouncement(Long id) {
        LambdaQueryChainWrapper<AnnouncementEntity> wrapper = lambdaQueryWrapper()
            .eq(AnnouncementEntity::getId, id)
            .eq(AnnouncementEntity::getActive, ActiveTypeEnum.ENABLED.getValue());

        return AnnouncementConverter.INSTANCE.entity2Vo(this.findOneByWrapper(wrapper));
    }

    /**
     * @see AnnouncementService#saveAnnouncement(AnnouncementForm)
     */
    @Override
    public void saveAnnouncement(AnnouncementForm form) {
        form.setStartDatetime(nvl(form.getStartDatetime(), MIN_DATETIME));
        form.setEndDatetime(nvl(form.getEndDatetime(), MAX_DATETIME));

        AnnouncementEntity entity;
        if (form.getId() != null && form.getId() > 0) {
            entity = this.findById(form.getId());
            ObjectUtils.copyNotNullProperties(form, entity);
        } else {
            entity = AnnouncementConverter.INSTANCE.form2Entity(form);
        }
        entity.setActive(ActiveTypeEnum.ENABLED.getValue());
        this.save(entity);
    }

}
