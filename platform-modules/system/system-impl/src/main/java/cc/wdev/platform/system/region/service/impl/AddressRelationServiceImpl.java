package cc.wdev.platform.system.region.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.utils.ArrayUtils;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.region.domain.entity.AddressRelationEntity;
import cc.wdev.platform.system.region.domain.request.AddressDeleteRequest;
import cc.wdev.platform.system.region.repository.AddressRelationRepository;
import cc.wdev.platform.system.region.service.AddressRelationService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author erden
 */
@Slf4j
@Service
public class AddressRelationServiceImpl extends BaseCachingEntityService<AddressRelationEntity, Long, AddressRelationRepository> implements AddressRelationService {

    @Override
    public List<AddressRelationEntity> findRelations(RelationRequest request) {
        List<Long> bizIds = Lists.newArrayList();
        if (ObjectUtils.isValidId(request.getBizId())) {
            bizIds.add(request.getBizId());
        }
        if (CollectionUtils.isNotEmpty(request.getBizIdList())) {
            bizIds.addAll(request.getBizIdList());
        }
        if (CollectionUtils.isEmpty(bizIds)) {
            return Collections.emptyList();
        }
        return lambdaQueryWrapper()
            .eq(request.getRelationBizType() != null, AddressRelationEntity::getBizType, request.getRelationBizType())
            .in(AddressRelationEntity::getBizId, bizIds)
            .list();
    }

    @Override
    public List<Long> findBizIdsByAddressId(Long addressId, String relationBizType) {
        if (!ObjectUtils.isValidId(addressId) || StringUtils.isBlank(relationBizType)) {
            return Collections.emptyList();
        }
        List<AddressRelationEntity> list = lambdaQueryWrapper()
            .eq(AddressRelationEntity::getAddressId, addressId)
            .eq(StringUtils.isNotEmpty(relationBizType), AddressRelationEntity::getBizType, relationBizType)
            .list();
        return list.stream().map(AddressRelationEntity::getBizId).distinct().toList();
    }

    @Override
    public void deleteRelation(RelationRequest request) {
        List<AddressRelationEntity> relationEntityList = this.findRelations(request);
        if (CollectionUtils.isNotEmpty(relationEntityList)) {
            this.deleteBatch(relationEntityList);
        }
    }

    @Override
    public Boolean hasRelated(AddressDeleteRequest request) {
        if (ArrayUtils.isEmpty(request.getIds())) {
            return Boolean.FALSE;
        }
        return lambdaQueryWrapper()
            .in(AddressRelationEntity::getAddressId, Arrays.asList(request.getIds()))
            .exists();
    }

    @Override
    public void saveRelation(RelationSaveRequest request) {
        String relationType = request.getRelationBizType();
        if (StringUtils.isNotEmpty(relationType)) {
            this.deleteRelation(RelationRequest.builder()
                .relationBizType(relationType)
                .bizType(request.getBizType())
                .bizId(request.getBizId())
                .build()
            );
        }

        List<AddressRelationEntity> entityList = Arrays.stream(request.getIds()).map((id) -> AddressRelationEntity.builder()
            .bizType(request.getRelationBizType())
            .bizId(request.getBizId())
            .addressId(id)
            .build()
        ).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(entityList)) {
            saveBatch(entityList);
            log.info("AddressRelation save success.");
        }
    }

}
