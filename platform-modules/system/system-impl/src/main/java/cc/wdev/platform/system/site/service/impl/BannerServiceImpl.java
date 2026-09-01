package cc.wdev.platform.system.site.service.impl;

import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.GsonUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.dict.api.DictApi;
import cc.wdev.platform.system.dict.enums.DictBizTypeEnum;
import cc.wdev.platform.system.dict.enums.DictRelationBizTypeEnum;
import cc.wdev.platform.system.dict.service.DictRelationService;
import cc.wdev.platform.system.site.domain.converter.BannerConverter;
import cc.wdev.platform.system.site.domain.entity.BannerEntity;
import cc.wdev.platform.system.site.domain.form.BannerForm;
import cc.wdev.platform.system.site.domain.request.BannerSearchRequest;
import cc.wdev.platform.system.site.domain.vo.BannerVo;
import cc.wdev.platform.system.site.domain.vo.webapp.BannerWebappVo;
import cc.wdev.platform.system.site.repository.BannerRepository;
import cc.wdev.platform.system.site.service.BannerService;
import cc.wdev.platform.system.storage.api.AttachmentApi;
import cc.wdev.platform.system.storage.domain.entity.AttachmentEntity;
import cc.wdev.platform.system.storage.domain.request.AttachmentRelationRequest;
import cc.wdev.platform.system.storage.domain.request.AttachmentRequest;
import cc.wdev.platform.system.storage.domain.vo.webapp.AttachmentFileVo;
import cc.wdev.platform.system.storage.domain.vo.webapp.AttachmentVo;
import cc.wdev.platform.system.storage.enums.AttachmentBizTypeEnum;
import cc.wdev.platform.system.storage.enums.AttachmentRelationBizTypeEnum;
import cc.wdev.platform.system.storage.service.AttachmentRelationService;
import cc.wdev.platform.system.storage.service.AttachmentService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.toSpringDataPage;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class BannerServiceImpl extends BaseCachingEntityService<BannerEntity, Long, BannerRepository> implements BannerService {

    private DictApi dictApi;

    private DictRelationService dictRelationService;

    private AttachmentApi attachmentApi;

    private AttachmentService attachmentService;

    private AttachmentRelationService attachmentRelationService;

    /**
     * @see BannerService#saveBanner(BannerForm)
     */
    @Override
    public void saveBanner(BannerForm form) {
        BannerEntity entity;

        if (form.getId() != null && form.getId() > 0) {
            entity = this.findById(form.getId());
            ObjectUtils.copyNotNullProperties(form, entity);
        } else {
            entity = BannerConverter.INSTANCE.formToEntity(form);
        }
        entity.setActive(1);
        this.save(entity);

        // 保存类型字典关联
        dictApi.saveRelation(RelationSaveRequest.builder()
            .relationBizType(DictRelationBizTypeEnum.BANNER.getValue())
            .bizType(DictBizTypeEnum.BANNER.getCode())
            .bizId(entity.getId())
            .ids(form.getType().getIds())
            .build());
        // 保存封面附件关联
        attachmentApi.saveAttachmentRelation(AttachmentRelationRequest.builder()
            .bizType(AttachmentBizTypeEnum.BANNER_COVER.getValue())
            .bizId(entity.getId())
            .relationBizType(AttachmentRelationBizTypeEnum.BANNER_COVER.getValue())
            .attachmentIdList(form.getCover().getIds())
            .build()
        );
        // 保存封面附件额外信息
        if (!ObjectUtils.isEmpty(form.getCover().getConfig())) {
            List<AttachmentEntity> attachmentEntities = attachmentService.findByIds(form.getCover().getIds());
            attachmentEntities.forEach(attachment -> attachment.setExtra(GsonUtils.toJson(form.getCover().getConfig())));
            attachmentService.saveBatch(attachmentEntities);
        }

        // 保存移动端封面附件关联
        attachmentApi.saveAttachmentRelation(AttachmentRelationRequest.builder()
            .bizType(AttachmentBizTypeEnum.BANNER_MOBILE_COVER.getValue())
            .bizId(entity.getId())
            .relationBizType(AttachmentRelationBizTypeEnum.BANNER_MOBILE_COVER.getValue())
            .attachmentIdList(form.getMobileCover().getIds())
            .build()
        );
        // 保存移动端封面附件额外信息
        if (!ObjectUtils.isEmpty(form.getMobileCover().getConfig())) {
            List<AttachmentEntity> attachmentEntities = attachmentService.findByIds(form.getMobileCover().getIds());
            attachmentEntities.forEach(attachment -> attachment.setExtra(GsonUtils.toJson(form.getMobileCover().getConfig())));
            attachmentService.saveBatch(attachmentEntities);
        }
    }

    /**
     * @see EntityService#deleteById(Serializable)
     */
    @Override
    public void deleteById(Long id) {
        BannerEntity courseEntity = findById(id);

        if (!ObjectUtils.isEmpty(courseEntity)) {
            courseEntity.setActive(ActiveTypeEnum.DISABLED.getValue());
            save(courseEntity);

            // 删除字典关联
            dictRelationService.deleteRelation(RelationRequest.builder().relationBizType(DictRelationBizTypeEnum.BANNER.getValue()).bizType(DictBizTypeEnum.BANNER.getCode()).bizId(id).build());
            // 删除附件关联
            attachmentRelationService.deleteAttachmentRelation(AttachmentRelationRequest.builder()
                .relationBizType(AttachmentRelationBizTypeEnum.BANNER_COVER.getValue())
                .bizIdList(List.of(id))
                .build());
            attachmentRelationService.deleteAttachmentRelation(AttachmentRelationRequest.builder()
                .relationBizType(AttachmentRelationBizTypeEnum.BANNER_MOBILE_COVER.getValue())
                .bizIdList(List.of(id))
                .build());
        }
    }

    /**
     * @see BannerService#getExtra(BannerVo)
     */
    @Override
    public void getExtra(BannerVo banner) {
        if (null != banner && existsById(banner.getId())) {
            // 获取类型
            banner.setType(dictApi.getRelation(RelationRequest.builder().relationBizType(DictRelationBizTypeEnum.BANNER.getValue()).bizType(DictBizTypeEnum.BANNER.getCode()).bizId(banner.getId()).build()));
            // 获取封面
            banner.setCover(attachmentApi.getAttachment(AttachmentRequest.builder()
                .bizType(AttachmentBizTypeEnum.BANNER_COVER.getCode())
                .relationBizType(AttachmentRelationBizTypeEnum.BANNER_COVER.getValue())
                .bizId(banner.getId())
                .build()));
            // 获取移动端封面
            banner.setMobileCover(attachmentApi.getAttachment(AttachmentRequest.builder()
                .bizType(AttachmentBizTypeEnum.BANNER_MOBILE_COVER.getCode())
                .relationBizType(AttachmentRelationBizTypeEnum.BANNER_MOBILE_COVER.getValue())
                .bizId(banner.getId())
                .build()));
        }
    }

    /**
     * @see BannerService#findBannerForUser(BannerSearchRequest)
     */
    @Override
    public Page<BannerVo> findBannerForUser(@NonNull BannerSearchRequest searchRequest) {
        Map<String, Object> condition = Maps.newConcurrentMap();
        if (null != searchRequest.getItemCodes() && searchRequest.getItemCodes().length > 0) {
            condition.put("itemCodes", Arrays.asList(searchRequest.getItemCodes()));
        }
        if (StringUtils.isNotBlank(searchRequest.getQ())) {
            condition.put("q", searchRequest.getQ());
        }
        IPage<BannerEntity> page = mapper.findForUser(getMyBatisPlusPage(searchRequest.getPageable()), condition);
        List<BannerVo> vos = page.getRecords().stream().map(BannerConverter.INSTANCE::entityToVo).toList();
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            vos.forEach(this::getExtra);
        }
        return toSpringDataPage(searchRequest.getPageable(), vos, page.getTotal());
    }

    /**
     * @see BannerService#search(BannerSearchRequest)
     * 根据标题关键字返回宣传栏列表
     */
    @Override
    public Page<BannerEntity> search(BannerSearchRequest searchRequest) {
        IPage<BannerEntity> page = this.lambdaQueryWrapper().and(StringUtils.isNotBlank(searchRequest.getQ()),
                wrapper -> wrapper.like(BannerEntity::getTitle, searchRequest.getQ()))
            .page(getMyBatisPlusPage(searchRequest.getPageable()));
        return MyBatisPlusUtils.toSpringDataPage(page);
    }

    @Override
    public BannerVo getBanner(Long id) {
        LambdaQueryChainWrapper<BannerEntity> wrapper = lambdaQueryWrapper()
            .eq(BannerEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(BannerEntity::getId, id);

        BannerEntity entity = this.findOneByWrapper(wrapper);
        BannerVo bannerVo = BannerConverter.INSTANCE.entityToVo(entity);
        this.getExtra(bannerVo);
        return bannerVo;
    }

    @Override
    public BannerWebappVo getWebappBanner(Long id) {
        BannerEntity entity = findById(id);
        if (ObjectUtils.isEmpty(entity)) {
            return null;
        }
        BannerVo tempVo = BannerConverter.INSTANCE.entityToVo(entity);
        this.getExtra(tempVo);
        entity.setCover(tempVo.getCover());
        entity.setMobileCover(tempVo.getMobileCover());
        return toWebappVo(entity);
    }

    @Override
    public Page<BannerWebappVo> findBannerForWebapp(BannerSearchRequest searchRequest) {
        Map<String, Object> condition = Maps.newConcurrentMap();
        if (null != searchRequest.getItemCodes() && searchRequest.getItemCodes().length > 0) {
            condition.put("itemCodes", Arrays.asList(searchRequest.getItemCodes()));
        }
        if (StringUtils.isNotBlank(searchRequest.getQ())) {
            condition.put("q", searchRequest.getQ());
        }
        IPage<BannerEntity> page = mapper.findForUser(getMyBatisPlusPage(searchRequest.getPageable()), condition);
        List<BannerWebappVo> vos = page.getRecords().stream().map(entity -> {
            BannerVo tempVo = BannerConverter.INSTANCE.entityToVo(entity);
            this.getExtra(tempVo);
            entity.setCover(tempVo.getCover());
            entity.setMobileCover(tempVo.getMobileCover());
            return toWebappVo(entity);
        }).toList();
        return toSpringDataPage(searchRequest.getPageable(), vos, page.getTotal());
    }

    private BannerWebappVo toWebappVo(BannerEntity entity) {
        BannerWebappVo vo = BannerWebappVo.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .idx(entity.getIdx())
            .build();
        if (!ObjectUtils.isEmpty(entity.getCover())) {
            vo.setWebappCover(AttachmentVo.builder()
                .bizType(entity.getCover().getBizType())
                .bizId(entity.getCover().getBizId())
                .files(toAttachmentFileVoList(entity.getCover().getFiles()))
                .build());
        }
        if (!ObjectUtils.isEmpty(entity.getMobileCover())) {
            vo.setMobileCover(AttachmentVo.builder()
                .bizType(entity.getMobileCover().getBizType())
                .bizId(entity.getMobileCover().getBizId())
                .files(toAttachmentFileVoList(entity.getMobileCover().getFiles()))
                .build());
        }
        return vo;
    }

    private List<AttachmentFileVo> toAttachmentFileVoList(
        List<cc.wdev.platform.system.storage.domain.vo.AttachmentFileVo> files) {
        if (ObjectUtils.isEmpty(files)) {
            return List.of();
        }
        return files.stream()
            .map(f -> AttachmentFileVo.builder()
                .id(f.getId())
                .url(f.getUrl())
                .build())
            .toList();
    }

    @Override
    public Page<BannerVo> findPageBanner(BannerSearchRequest request) {
        IPage<BannerEntity> page = lambdaQueryWrapper()
            .eq(BannerEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .like(StringUtils.isNotBlank(request.getQ()), BannerEntity::getTitle, request.getQ())
            .page(getMyBatisPlusPage(request.getPageable()));

        if (!MyBatisPlusUtils.isNotEmpty(page)) {
            return SpringDataUtils.emptyPage(request.getPageable());
        }
        List<BannerVo> vos = page.getRecords().stream().map(BannerConverter.INSTANCE::entityToVo).toList();
        vos.forEach(this::getExtra);

        return toSpringDataPage(request.getPageable(), vos, page.getTotal());
    }

    @Override
    public void deleteBanner(Long id) {
        softDeleteById(id);

        //刪除字典关联
        dictRelationService.deleteRelation(RelationRequest.builder().relationBizType(DictRelationBizTypeEnum.BANNER.getValue()).bizType(DictBizTypeEnum.BANNER.getCode()).bizId(id).build());
        // 删除附件关联
        attachmentRelationService.deleteAttachmentRelation(AttachmentRelationRequest.builder()
            .relationBizType(AttachmentRelationBizTypeEnum.BANNER_COVER.getValue())
            .bizIdList(List.of(id))
            .build());
        attachmentRelationService.deleteAttachmentRelation(AttachmentRelationRequest.builder()
            .relationBizType(AttachmentRelationBizTypeEnum.BANNER_MOBILE_COVER.getValue())
            .bizIdList(List.of(id))
            .build());
    }

}
