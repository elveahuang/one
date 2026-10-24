package cc.wdev.platform.system.storage.service.impl;

import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.storage.domain.entity.AttachmentEntity;
import cc.wdev.platform.system.storage.domain.entity.AttachmentRelationEntity;
import cc.wdev.platform.system.storage.domain.request.AttachmentSearchRequest;
import cc.wdev.platform.system.storage.repository.AttachmentRelationRepository;
import cc.wdev.platform.system.storage.repository.AttachmentRepository;
import cc.wdev.platform.system.storage.service.AttachmentService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class AttachmentServiceImpl
    extends BaseCachingEntityService<AttachmentEntity, Long, AttachmentRepository> implements AttachmentService {

    private final AttachmentRelationRepository attachmentRelationRepository;

    /**
     * @see AttachmentService#findByPage(AttachmentSearchRequest)
     */
    @Override
    public Page<AttachmentEntity> findByPage(AttachmentSearchRequest request) {
        LambdaQueryChainWrapper<AttachmentEntity> wrapper = lambdaQueryWrapper()
            .and(StringUtils.isNotEmpty(request.getQ()), w -> w
                .like(AttachmentEntity::getOriginalFilename, request.getQ())
                .or().like(AttachmentEntity::getFilename, request.getQ()))
            .eq(StringUtils.isNotEmpty(request.getBizType()), AttachmentEntity::getBizType, request.getBizType())
            .eq(AttachmentEntity::getActive, ActiveTypeEnum.ENABLED.getValue());
        applySort(request.getSort(), request.getOrder(), wrapper);
        IPage<AttachmentEntity> page = wrapper.page(getMyBatisPlusPage(request.getPageable()));
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            return MyBatisPlusUtils.toSpringDataPage(page);
        }
        return SpringDataUtils.emptyPage();
    }

    /**
     * 应用排序条件，排序字段仅允许白名单内的列
     */
    private static void applySort(String sort, String order, LambdaQueryChainWrapper<AttachmentEntity> wrapper) {
        boolean asc = !"desc".equalsIgnoreCase(order);
        switch (StringUtils.isNotEmpty(sort) ? sort : "id") {
            case "original_filename", "originalFilename" -> wrapper.orderBy(true, asc, AttachmentEntity::getOriginalFilename);
            case "size" -> wrapper.orderBy(true, asc, AttachmentEntity::getSize);
            case "created_at", "createdAt" -> wrapper.orderBy(true, asc, AttachmentEntity::getCreatedAt);
            default -> wrapper.orderByDesc(AttachmentEntity::getId);
        }
    }

    /**
     * @see AttachmentService#deleteAttachments(List)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAttachments(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        // 删除附件关联
        this.attachmentRelationRepository.delete(new LambdaUpdateWrapper<AttachmentRelationEntity>()
            .in(AttachmentRelationEntity::getAttachmentId, ids));
        // 软删除附件
        this.softDeleteBatchById(ids);
    }

}
