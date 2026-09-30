package cc.wdev.platform.system.storage.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.system.storage.domain.entity.AttachmentRelationEntity;
import cc.wdev.platform.system.storage.domain.request.AttachmentRelationRequest;
import cc.wdev.platform.system.storage.repository.AttachmentRelationRepository;
import cc.wdev.platform.system.storage.service.AttachmentRelationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class AttachmentRelationServiceImpl
    extends BaseCachingEntityService<AttachmentRelationEntity, Long, AttachmentRelationRepository> implements AttachmentRelationService {

    /**
     * @see AttachmentRelationService#getAttachmentRelation(AttachmentRelationRequest)
     */
    @Override
    public List<AttachmentRelationEntity> getAttachmentRelation(AttachmentRelationRequest request) {
        if (CollectionUtils.isEmpty(request.getBizIdList())) {
            return Collections.emptyList();
        }

        return this.lambdaQueryWrapper()
            .eq(AttachmentRelationEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .in(AttachmentRelationEntity::getBizId, request.getBizIdList())
            .eq(AttachmentRelationEntity::getBizType, request.getRelationBizType())
            .list();
    }

    /**
     * @see AttachmentRelationService#deleteAttachmentRelation(AttachmentRelationRequest)
     */
    @Override
    public void deleteAttachmentRelation(AttachmentRelationRequest request) {
        if (CollectionUtils.isEmpty(request.getBizIdList())) {
            return;
        }

        this.lambdaUpdateWrapper()
            .eq(AttachmentRelationEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .in(AttachmentRelationEntity::getBizId, request.getBizIdList())
            // sys_attachment_relation.biz_type 落库的是 relationBizType（见 AttachmentApiImpl#saveAttachmentRelation），
            // 与请求里的附件类型 bizType 不是同一个值
            .eq(AttachmentRelationEntity::getBizType, request.getRelationBizType())
            // 关联表属 AGENTS.md「数据层」中"保存即整组重写"的例外清单：软删会随每次保存累积失效行，
            // 且读取侧（如 BannerRepository/LinkRepository 的 sys_dict_relation EXISTS 子查询）不过滤 active
            .remove();
    }

}
