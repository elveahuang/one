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
        this.lambdaUpdateWrapper()
            .eq(AttachmentRelationEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .in(AttachmentRelationEntity::getBizId, request.getBizIdList())
            .eq(AttachmentRelationEntity::getBizType, request.getBizType())
            .remove();
    }

}
