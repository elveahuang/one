package cc.wdev.platform.system.site.service.impl;

import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.commons.utils.GsonUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.dict.api.DictApi;
import cc.wdev.platform.system.dict.enums.DictBizTypeEnum;
import cc.wdev.platform.system.dict.enums.DictRelationBizTypeEnum;
import cc.wdev.platform.system.dict.service.DictRelationService;
import cc.wdev.platform.system.site.domain.converter.LinkConverter;
import cc.wdev.platform.system.site.domain.entity.LinkEntity;
import cc.wdev.platform.system.site.domain.form.LinkForm;
import cc.wdev.platform.system.site.domain.request.LinkSearchRequest;
import cc.wdev.platform.system.site.domain.vo.LinkVo;
import cc.wdev.platform.system.site.repository.LinkRepository;
import cc.wdev.platform.system.site.service.LinkService;
import cc.wdev.platform.system.storage.api.AttachmentApi;
import cc.wdev.platform.system.storage.domain.entity.AttachmentEntity;
import cc.wdev.platform.system.storage.domain.request.AttachmentRelationRequest;
import cc.wdev.platform.system.storage.domain.request.AttachmentRequest;
import cc.wdev.platform.system.storage.enums.AttachmentBizTypeEnum;
import cc.wdev.platform.system.storage.enums.AttachmentRelationBizTypeEnum;
import cc.wdev.platform.system.storage.service.AttachmentRelationService;
import cc.wdev.platform.system.storage.service.AttachmentService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class LinkServiceImpl extends BaseCachingEntityService<LinkEntity, Long, LinkRepository> implements LinkService {

    private DictApi dictApi;

    private AttachmentApi attachmentApi;

    private AttachmentService attachmentService;

    private DictRelationService dictRelationService;

    private AttachmentRelationService attachmentRelationService;

    @Override
    public void saveLink(LinkForm form) {
        LinkEntity entity;

        if (form.getId() != null && form.getId() > 0) {
            entity = this.findById(form.getId());
            ObjectUtils.copyNotNullProperties(form, entity);
        } else {
            entity = LinkConverter.INSTANCE.formToEntity(form);
        }
        entity.setActive(ActiveTypeEnum.ENABLED.getValue());
        this.save(entity);

        // 保存类型字典关联
        dictApi.saveRelation(RelationSaveRequest.builder()
            .relationBizType(DictRelationBizTypeEnum.LINK.getValue())
            .bizType(DictRelationBizTypeEnum.LINK.getValue())
            .bizId(entity.getId())
            .ids(form.getType().getIds())
            .build());

        // 保存分类字典关联
        if (form.getLinkType().getIds() != null) {
            dictApi.saveRelation(RelationSaveRequest.builder()
                .relationBizType(DictRelationBizTypeEnum.LINK_CATALOG.getValue())
                .bizType(DictRelationBizTypeEnum.LINK_CATALOG.getValue())
                .bizId(entity.getId())
                .ids(form.getLinkType().getIds())
                .build());
        }

        // 保存封面附件关联
        attachmentApi.saveAttachmentRelation(AttachmentRelationRequest.builder()
            .bizType(AttachmentBizTypeEnum.LINK_COVER.getValue())
            .bizId(entity.getId())
            .relationBizType(AttachmentRelationBizTypeEnum.LINK_COVER.getValue())
            .attachmentIdList(List.of(form.getCover().getIds().getLast()))
            .build()
        );

        // 保存封面附件额外信息
        if (!ObjectUtils.isEmpty(form.getCover().getConfig())) {
            List<AttachmentEntity> attachmentEntities = attachmentService.findByIds(form.getCover().getIds());
            attachmentEntities.forEach(attachment -> attachment.setExtra(GsonUtils.toJson(form.getCover().getConfig())));
            attachmentService.saveBatch(attachmentEntities);
        }
    }

    /**
     * @see EntityService#deleteById(Serializable)
     */
    @Override
    public void deleteById(Long id) {
        LinkEntity entity = findById(id);
        if (ObjectUtils.isEmpty(entity)) {
            return;
        }

        this.softDelete(entity);

        // 删除字典关联
        dictRelationService.deleteRelation(RelationRequest.builder().relationBizType(DictRelationBizTypeEnum.LINK.getValue()).bizType(DictBizTypeEnum.LINK.getCode()).bizId(id).build());
        // 删除附件关联
        attachmentRelationService.deleteAttachmentRelation(AttachmentRelationRequest.builder()
            .relationBizType(AttachmentRelationBizTypeEnum.LINK_COVER.getValue())
            .bizIdList(List.of(id))
            .build());
    }

    @Override
    public void getExtra(LinkVo entity) {
        if (null != entity && existsById(entity.getId())) {
            // 获取类型
            entity.setType(dictApi.getRelation(RelationRequest.builder().relationBizType(DictRelationBizTypeEnum.LINK.getValue()).bizType(DictBizTypeEnum.LINK.getCode()).bizId(entity.getId()).build()));
            // 获取分类
            entity.setLinkType(dictApi.getRelation(RelationRequest.builder().relationBizType(DictRelationBizTypeEnum.LINK_CATALOG.getValue()).bizType(DictBizTypeEnum.LINK_CATALOG.getCode()).bizId(entity.getId()).build()));
            // 获取封面
            // 获取移动端封面
            entity.setCover(attachmentApi.getAttachment(AttachmentRequest.builder()
                .bizType(AttachmentBizTypeEnum.LINK_COVER.getCode())
                .relationBizType(AttachmentRelationBizTypeEnum.LINK_COVER.getValue())
                .bizId(entity.getId())
                .build()));
        }
    }

    /**
     * 移动端获取友情链接
     */
    @Override
    public Page<LinkVo> friendLinkList(LinkSearchRequest request) {
        Map<String, Object> condition = Maps.newConcurrentMap();
        if (null != request.getItemCodes() && request.getItemCodes().length > 0) {
            condition.put("itemCodes", Arrays.asList(request.getItemCodes()));
        }
        if (StringUtils.isNotEmpty(request.getQ())) {
            condition.put("q", request.getQ());
        }
        IPage<LinkEntity> page = mapper.friendLinkList(getMyBatisPlusPage(request.getPageable()), condition);
        if (!MyBatisPlusUtils.isNotEmpty(page)) {
            return SpringDataUtils.emptyPage(request.getPageable());
        }
        List<LinkVo> vos = page.getRecords().stream().map(LinkConverter.INSTANCE::entityToVo).toList();
        vos.forEach(this::getExtra);
        return MyBatisPlusUtils.toSpringDataPage(request.getPageable(), vos, page.getTotal());
    }

    /**
     * 根据标题查询友情链接列表
     */
    @Override
    public Page<LinkVo> friendLinksByKeyword(LinkSearchRequest request) {
        IPage<LinkEntity> page = this.lambdaQueryWrapper()
            .and(StringUtils.isNotEmpty(request.getQ()), wrapper -> {
                wrapper.like(LinkEntity::getTitle, request.getQ());
            })
            .eq(LinkEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .page(getMyBatisPlusPage(request.getPageable()));

        if (!MyBatisPlusUtils.isNotEmpty(page)) {
            return SpringDataUtils.emptyPage(request.getPageable());
        }
        List<LinkVo> vos = page.getRecords().stream().map(LinkConverter.INSTANCE::entityToVo).toList();
        vos.forEach(this::getExtra);
        return MyBatisPlusUtils.toSpringDataPage(request.getPageable(), vos, page.getTotal());
    }


    @Override
    public LinkVo getLink(Long id) {
        LambdaQueryChainWrapper<LinkEntity> wrapper = lambdaQueryWrapper()
            .eq(LinkEntity::getId, id)
            .eq(LinkEntity::getActive, ActiveTypeEnum.ENABLED.getValue());

        LinkEntity entity = this.findOneByWrapper(wrapper);
        if (entity == null) {
            return null;
        }
        LinkVo linkVo = LinkConverter.INSTANCE.entityToVo(entity);
        this.getExtra(linkVo);
        return linkVo;
    }

    @Override
    public void deleteLink(Long id) {
        softDeleteById(id);

        // 删除字典关联
        dictRelationService.deleteRelation(RelationRequest.builder().relationBizType(DictRelationBizTypeEnum.LINK.getValue()).bizType(DictBizTypeEnum.LINK.getCode()).bizId(id).build());
        // 删除分类字典关联
        dictRelationService.deleteRelation(RelationRequest.builder().relationBizType(DictRelationBizTypeEnum.LINK_CATALOG.getValue()).bizType(DictBizTypeEnum.LINK_CATALOG.getCode()).bizId(id).build());
        // 删除附件关联
        attachmentRelationService.deleteAttachmentRelation(AttachmentRelationRequest.builder()
            .relationBizType(AttachmentRelationBizTypeEnum.LINK_COVER.getValue())
            .bizType(AttachmentBizTypeEnum.LINK_COVER.getCode())
            .bizIdList(List.of(id))
            .build());
    }

}
