package cc.wdev.platform.system.storage.api;

import cc.wdev.platform.commons.core.storage.StorageFactory;
import cc.wdev.platform.commons.core.storage.model.FileObject;
import cc.wdev.platform.commons.core.storage.model.FileOptions;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.enums.BaseEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.enums.StorageAccessTypeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.*;
import cc.wdev.platform.system.commons.enums.CoreBizGroupTypeEnum;
import cc.wdev.platform.system.config.api.BizTypeApi;
import cc.wdev.platform.system.config.domain.vo.BizTypeVo;
import cc.wdev.platform.system.storage.domain.biz.Config;
import cc.wdev.platform.system.storage.domain.entity.AttachmentEntity;
import cc.wdev.platform.system.storage.domain.entity.AttachmentRelationEntity;
import cc.wdev.platform.system.storage.domain.request.AttachmentRelationRequest;
import cc.wdev.platform.system.storage.domain.request.AttachmentRequest;
import cc.wdev.platform.system.storage.domain.vo.AttachmentFileVo;
import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import cc.wdev.platform.system.storage.service.AttachmentRelationService;
import cc.wdev.platform.system.storage.service.AttachmentService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.enums.StorageAccessTypeEnum.PRIVATE;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class AttachmentApiImpl implements AttachmentApi {

    private final StorageFactory storageFactory;

    private final BizTypeApi bizTypeApi;

    private final AttachmentService attachmentService;

    private final AttachmentRelationService attachmentRelationService;

    /**
     * @see AttachmentApi#getAttachmentType(AttachmentRequest)
     */
    @Override
    public AttachmentVo getAttachmentType(AttachmentRequest request) {
        // 获取业务类型配置
        BizTypeVo<Config> bizTypeVo = this.bizTypeApi.getBizType(
            CoreBizGroupTypeEnum.ATTACHMENT_TYPE.getValue(), StringUtils.nvl(request.getBizType()), Config.class);

        // 错误业务类型直接抛出异常
        if (StringUtils.isEmpty(bizTypeVo.getBizType())) {
            throw new ServiceException(ResponseCodeEnum.BAD_REQUEST);
        }

        // 这里后续需要增加参数配置合并
        return AttachmentVo.builder().bizType(bizTypeVo.getBizType()).config(bizTypeVo.getConfig()).build();
    }

    /**
     * @see AttachmentApi#getAttachment(AttachmentRequest)
     */
    @Override
    public AttachmentVo getAttachment(AttachmentRequest request) {
        AttachmentVo attachmentVo = this.getAttachmentType(request);

        List<AttachmentFileVo> attachmentFileList = this.getAttachmentFile(request);
        attachmentVo.setIds(attachmentFileList.stream().map(AttachmentFileVo::getId).collect(Collectors.toList()));
        attachmentVo.setFiles(attachmentFileList);
        return attachmentVo;
    }

    /**
     * @see AttachmentApi#uploadAttachment(AttachmentRequest, MultipartFile)
     */
    @Override
    @Transactional
    public AttachmentFileVo uploadAttachment(AttachmentRequest request, MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            // 获取业务类型配置
            AttachmentVo attachmentTypeVo = this.getAttachmentType(request);
            StorageAccessTypeEnum accessType = BaseEnum.getEnumByValue(attachmentTypeVo.getConfig().getAccessType(), StorageAccessTypeEnum.class, PRIVATE);

            // 生成上传参数
            FileOptions options = FileOptions.builder()
                .originalFilename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .accessType(accessType)
                .build();

            // 处理文件上传
            return this.uploadAttachment(request, is, options);
        } catch (Exception e) {
            log.error("uploadAttachmentFile failed.", e);
            throw new ServiceException(ResponseCodeEnum.BAD_REQUEST);
        }
    }

    /**
     * @see AttachmentApi#uploadAttachment(AttachmentRequest, MultipartFile)
     */
    @Override
    @Transactional
    public AttachmentFileVo uploadAttachment(AttachmentRequest request, Resource resource) {
        try (InputStream is = resource.getInputStream()) {
            // 获取业务类型配置
            AttachmentVo attachmentTypeVo = this.getAttachmentType(request);
            StorageAccessTypeEnum accessType = BaseEnum.getEnumByValue(attachmentTypeVo.getConfig().getAccessType(), StorageAccessTypeEnum.class, PRIVATE);

            // 生成上传参数
            FileOptions options = FileOptions.builder()
                .originalFilename(resource.getFilename())
                .contentType(FileUtils.getContentType(resource.getFile()))
                .size(FileUtils.getFileSize(resource.getFile()))
                .accessType(accessType)
                .build();

            // 处理文件上传
            return this.uploadAttachment(request, is, options);
        } catch (Exception e) {
            log.error("uploadAttachmentFile failed.", e);
            throw new ServiceException(ResponseCodeEnum.BAD_REQUEST);
        }
    }

    /**
     * @see AttachmentApi#uploadAttachment(AttachmentRequest, File)
     */
    @Override
    @Transactional
    public AttachmentFileVo uploadAttachment(AttachmentRequest request, File file) {
        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(file))) {
            // 获取业务类型配置
            AttachmentVo attachmentTypeVo = this.getAttachmentType(request);
            StorageAccessTypeEnum accessType = BaseEnum.getEnumByValue(attachmentTypeVo.getConfig().getAccessType(), StorageAccessTypeEnum.class, PRIVATE);

            // 生成上传参数
            FileOptions options = FileOptions.builder()
                .originalFilename(file.getName())
                .contentType(FileUtils.getContentType(file))
                .size(FileUtils.getFileSize(file))
                .accessType(accessType)
                .build();

            // 处理文件上传
            return this.uploadAttachment(request, is, options);
        } catch (Exception e) {
            log.error("uploadAttachmentFile failed.", e);
            throw new ServiceException(ResponseCodeEnum.BAD_REQUEST);
        }
    }

    /**
     * @see AttachmentApi#uploadAttachment(AttachmentRequest, InputStream, FileOptions)
     */
    @Override
    @Transactional
    public AttachmentFileVo uploadAttachment(AttachmentRequest request, InputStream is, FileOptions options) {
        try {
            // 统一增加租户前缀
            if (StringUtils.isEmpty(options.getPrefix())) {
                options.setPrefix(TenantContext.getTenantIdAsString());
            }

            // 上传文件到文件存储
            FileObject<?> uploadFileObject = this.storageFactory.getStorageService().uploadFile(is, options);

            String url;
            if (PRIVATE.equals(options.getAccessType())) {
                // 生成签名链接
                FileObject<?> getUrlObject = this.storageFactory.getStorageService().getUrl(uploadFileObject.getKey());
                url = getUrlObject.getUrl();
            } else {
                // 生成开放链接
                url = this.storageFactory.getStorageService().getDomain() + "/" + uploadFileObject.getKey();
            }

            // 保存附件文件记录
            AttachmentEntity entity = AttachmentEntity.builder()
                .bizType(request.getBizType())
                .originalFilename(options.getOriginalFilename())
                .size(options.getSize())
                .contentType(options.getContentType())
                .storageType(uploadFileObject.getType().name())
                .fileKey(uploadFileObject.getKey())
                .url(url)
                .build();
            this.attachmentService.save(entity);

            return AttachmentFileVo.builder()
                .id(entity.getId())
                .filename(options.getFilename())
                .size(options.getSize())
                .contentType(options.getContentType())
                .originalFilename(options.getOriginalFilename())
                .bizType(request.getBizType())
                .key(uploadFileObject.getKey())
                .url(url)
                .build();
        } catch (Exception e) {
            log.error("uploadAttachmentFile failed.", e);
            throw new ServiceException(ResponseCodeEnum.BAD_REQUEST);
        }
    }

    /**
     * @see AttachmentApi#saveAttachmentRelation(AttachmentRelationRequest)
     */
    @Override
    @Transactional
    public void saveAttachmentRelation(AttachmentRelationRequest request) {
        if (request == null || StringUtils.isBlank(request.getRelationBizType())) {
            return;
        }

        // 清空关联
        attachmentRelationService.deleteAttachmentRelation(AttachmentRelationRequest.builder()
            .bizType(request.getBizType())
            .bizIdList(List.of(request.getBizId()))
            .relationBizType(request.getRelationBizType())
            .build()
        );

        // 保存关联
        if (CollectionUtils.isNotEmpty(request.getAttachmentIdList())) {
            for (Long attachmentId : request.getAttachmentIdList()) {
                AttachmentRelationEntity relation = AttachmentRelationEntity.builder()
                    .bizId(request.getBizId())
                    .bizType(request.getRelationBizType())
                    .attachmentId(attachmentId)
                    .build();
                attachmentRelationService.save(relation);

                // 如果有额外信息, 需要更新sys_attachment_file
                if (StringUtils.isNotEmpty(request.getExtra())) {
                    attachmentService.save(attachmentService.findById(attachmentId, (file) -> {
                            Map<String, Object> extra = GsonUtils.toObjectMap(file.getExtra());
                            extra.put("url", file.getUrl());
                            file.setExtra(GsonUtils.toJson(extra));
                        })
                    );
                }
            }
        }
    }

    /**
     * @see AttachmentApi#deleteAttachmentRelation(AttachmentRelationRequest)
     */
    @Override
    @Transactional
    public void deleteAttachmentRelation(AttachmentRelationRequest request) {
        if (request == null || CollectionUtils.isEmpty(request.getBizIdList())) {
            return;
        }
        // 查询关联，软删除附件文件
        List<AttachmentRelationEntity> relations = this.attachmentRelationService.getAttachmentRelation(
            AttachmentRelationRequest.builder()
                .bizIdList(request.getBizIdList())
                .relationBizType(request.getRelationBizType())
                .build());
        if (CollectionUtils.isNotEmpty(relations)) {
            List<Long> fileIds = relations.stream()
                .map(AttachmentRelationEntity::getAttachmentId)
                .distinct()
                .toList();
            if (CollectionUtils.isNotEmpty(fileIds)) {
                List<AttachmentEntity> files = this.attachmentService.findByIds(fileIds);
                if (CollectionUtils.isNotEmpty(files)) {
                    this.attachmentService.softDeleteBatch(files);
                }
            }
        }
        // 删除关联
        this.attachmentRelationService.deleteAttachmentRelation(AttachmentRelationRequest.builder()
            .bizType(request.getBizType())
            .bizIdList(request.getBizIdList())
            .relationBizType(request.getRelationBizType())
            .build());
    }

    /**
     * @see AttachmentApi#uploadAttachment(AttachmentRequest, MultipartFile)
     */
    @Override
    public List<AttachmentFileVo> getAttachmentFile(AttachmentRequest request) {
        String relationBizType = StringUtils.nvl(request.getRelationBizType(), request.getBizType()).trim();
        Long bizId = NumberUtils.nvl(request.getBizId());
        if (StringUtils.isEmpty(relationBizType) || bizId < 0) {
            return Collections.emptyList();
        }

        List<AttachmentRelationEntity> relationList = this.attachmentRelationService.getAttachmentRelation(AttachmentRelationRequest.builder()
            .relationBizType(relationBizType)
            .bizType(request.getBizType())
            .bizIdList(List.of(bizId))
            .build()
        );
        if (CollectionUtils.isEmpty(relationList)) {
            return Collections.emptyList();
        }

        List<Long> fileIdList = relationList.stream().map(AttachmentRelationEntity::getAttachmentId).toList();
        List<AttachmentEntity> fileList = this.attachmentService.findByIds(fileIdList);
        if (CollectionUtils.isEmpty(fileList)) {
            return Collections.emptyList();
        }

        if (request.getConfig() != null && PRIVATE.getValue().equalsIgnoreCase(request.getConfig().getAccessType())) {
            Map<String, String> preUrlMap = storageFactory.getStorageService()
                .presignedObjectUrlMap(fileList.stream().map(AttachmentEntity::getFileKey).collect(Collectors.toList()));
            return fileList.stream().map((e) -> AttachmentFileVo.builder()
                .bizType(e.getBizType())
                .originalFilename(e.getOriginalFilename())
                .url(preUrlMap.get(e.getFileKey()))
                .id(e.getId())
                .build()
            ).toList();
        }
        return fileList.stream().map((e) -> AttachmentFileVo.builder()
            .bizType(e.getBizType())
            .originalFilename(e.getOriginalFilename())
            .url(e.getUrl())
            .id(e.getId())
            .build()
        ).toList();
    }

    /**
     * @see AttachmentApi#getAttachmentBatch(AttachmentRequest)
     */
    @Override
    public Map<Long, AttachmentVo> getAttachmentBatch(AttachmentRequest request) {
        List<Long> bidIds = Lists.newArrayList();
        if (ObjectUtils.isValidId(request.getBizId())) {
            bidIds.add(request.getBizId());
        }
        if (CollectionUtils.isNotEmpty(request.getBizIdList())) {
            bidIds.addAll(request.getBizIdList());
        }
        if (CollectionUtils.isEmpty(bidIds)) {
            return Collections.emptyMap();
        }

        AttachmentVo attachmentTypeVo = this.getAttachmentType(request);
        request.setConfig(attachmentTypeVo.getConfig());

        // 查询文件关联
        List<AttachmentRelationEntity> relationList = attachmentRelationService.getAttachmentRelation(AttachmentRelationRequest.builder()
            .relationBizType(request.getRelationBizType())
            .bizIdList(bidIds)
            .build()
        );
        if (CollectionUtils.isEmpty(relationList)) {
            return Collections.emptyMap();
        }

        // 查询文件
        List<Long> attachmentIdList = relationList.stream().map(AttachmentRelationEntity::getAttachmentId).toList();
        List<AttachmentEntity> fileList = this.attachmentService.findByIds(attachmentIdList);
        if (CollectionUtils.isEmpty(fileList)) {
            return Collections.emptyMap();
        }


        Map<Long, List<AttachmentRelationEntity>> relationEntityMap = relationList.stream()
            .collect(Collectors.groupingBy(AttachmentRelationEntity::getBizId));
        Map<Long, AttachmentVo> attachmentMap = Maps.newHashMapWithExpectedSize(bidIds.size());

        // 批量获取fileUrl
        Map<String, String> fileUrlMap = Maps.newHashMapWithExpectedSize(fileList.size());
        Map<Long, AttachmentEntity> fileEntityMap = Maps.newHashMapWithExpectedSize(fileList.size());

        for (AttachmentEntity attachmentEntity : fileList) {
            fileUrlMap.put(attachmentEntity.getFileKey(), attachmentEntity.getUrl());
            fileEntityMap.put(attachmentEntity.getId(), attachmentEntity);
        }
        if (request.getConfig() != null && PRIVATE.getValue().equalsIgnoreCase(request.getConfig().getAccessType())) {
            fileUrlMap = storageFactory.getStorageService().presignedObjectUrlMap(fileUrlMap.keySet());
        }

        for (Long bizId : bidIds) {
            AttachmentVo attachment = this.getAttachmentType(AttachmentRequest.builder()
                .bizType(request.getBizType())
                .bizId(bizId)
                .build());

            List<AttachmentRelationEntity> relations = relationEntityMap.get(bizId);
            if (CollectionUtils.isEmpty(relations)) {
                attachmentMap.put(bizId, attachment);
                continue;
            }

            List<Long> fileIds = Lists.newArrayListWithCapacity(relations.size());
            List<AttachmentFileVo> files = Lists.newArrayListWithCapacity(relations.size());
            for (AttachmentRelationEntity relation : relations) {
                AttachmentEntity fileEntity = fileEntityMap.get(relation.getAttachmentId());
                if (fileEntity == null) {
                    continue;
                }
                AttachmentFileVo file = AttachmentFileVo.builder()
                    .bizType(fileEntity.getBizType())
                    .originalFilename(fileEntity.getOriginalFilename())
                    .extra(fileEntity.getExtra())
                    .url(fileUrlMap.get(fileEntity.getFileKey()))
                    .id(fileEntity.getId())
                    .build();
                fileIds.add(fileEntity.getId());
                files.add(file);
            }

            attachment.setIds(fileIds);
            attachment.setFiles(files);
            attachmentMap.put(bizId, attachment);
        }
        return attachmentMap;
    }

}
