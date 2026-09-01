package cc.wdev.platform.system.region.service.impl;

import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.*;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.region.annotation.TargetAddress;
import cc.wdev.platform.system.region.api.RegionApi;
import cc.wdev.platform.system.region.domain.convert.AddressConverter;
import cc.wdev.platform.system.region.domain.entity.AddressEntity;
import cc.wdev.platform.system.region.domain.entity.AddressRelationEntity;
import cc.wdev.platform.system.region.domain.form.AddressForm;
import cc.wdev.platform.system.region.domain.request.AddressDeleteRequest;
import cc.wdev.platform.system.region.domain.request.AddressRequest;
import cc.wdev.platform.system.region.domain.request.AddressSearchRequest;
import cc.wdev.platform.system.region.domain.vo.AddressVo;
import cc.wdev.platform.system.region.domain.vo.RegionVo;
import cc.wdev.platform.system.region.repository.AddressRepository;
import cc.wdev.platform.system.region.service.AddressRelationService;
import cc.wdev.platform.system.region.service.AddressService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;

/**
 * @author erden
 */
@Slf4j
@Service
@AllArgsConstructor
public class AddressServiceImpl extends BaseCachingEntityService<AddressEntity, Long, AddressRepository> implements AddressService {

    private final AddressRelationService addressRelationService;

    private final RegionApi regionApi;

    @Override
    public Page<AddressVo> findPageByBizType(AddressSearchRequest request) {
        IPage<AddressEntity> page;

        if (StringUtils.isNotEmpty(request.getRelationBizType())) {
            page = this.getMapper().findPageByRelation(getMyBatisPlusPage(request.getPageable()), request);
        } else {
            page = lambdaQueryWrapper()
                .eq(AddressEntity::getBizType, request.getBizType())
                .eq(AddressEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
                .eq(AddressEntity::getTenantId, request.getTenantId())
                .in(CollectionUtils.isNotEmpty(request.getBizIdList()), AddressEntity::getBizId, request.getBizIdList())
                .and(StringUtils.isNotEmpty(request.getQ()), w -> w
                    .like(AddressEntity::getTitle, request.getQ())
                    .or().like(AddressEntity::getDetails, request.getQ()))
                .page(getMyBatisPlusPage(request.getPageable()));
        }

        if (!MyBatisPlusUtils.isNotEmpty(page)) {
            return SpringDataUtils.emptyPage(request.getPageable());
        }

        List<AddressVo> vos = page.getRecords().stream().map(AddressConverter.INSTANCE::entity2Vo).toList();
        fillRegionNames(vos);
        return MyBatisPlusUtils.toSpringDataPage(request.getPageable(), vos, page.getTotal());
    }

    @Override
    public List<AddressVo> findListByBizType(AddressSearchRequest request) {
        List<AddressEntity> list = lambdaQueryWrapper()
            .eq(AddressEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(AddressEntity::getBizType, request.getBizType())
            .eq(AddressEntity::getTenantId, request.getTenantId())
            .in(CollectionUtils.isNotEmpty(request.getBizIdList()), AddressEntity::getBizId, request.getBizIdList())
            .and(StringUtils.isNotEmpty(request.getQ()), w -> w
                .like(AddressEntity::getTitle, request.getQ())
                .or().like(AddressEntity::getDetails, request.getQ()))
            .list();
        return list.stream().map(AddressConverter.INSTANCE::entity2Vo).toList();
    }

    @Override
    public Map<Long, RelationVo<AddressVo>> relationMap(RelationRequest request) {
        List<AddressRelationEntity> relationList = this.addressRelationService.findRelations(request);
        String relationBizType = request.getRelationBizType();
        if (StringUtils.isEmpty(relationBizType) && CollectionUtils.isNotEmpty(relationList)) {
            relationBizType = relationList.getFirst().getBizType();
        }
        Map<Long, List<AddressRelationEntity>> relationMap = Maps.newHashMap();
        List<Long> addressIds = Lists.newArrayListWithCapacity(relationList.size());
        for (AddressRelationEntity relation : relationList) {
            Long bizId = relation.getBizId();
            Long addressId = relation.getAddressId();
            addressIds.add(addressId);
            relationMap.putIfAbsent(bizId, Lists.newArrayList());
            relationMap.get(bizId).add(relation);
        }

        List<AddressEntity> entityList = this.findByIds(addressIds);
        Map<Long, AddressEntity> entityMap = entityList.stream().collect(Collectors.toMap(AddressEntity::getId, e -> e));

        List<AddressVo> addressVos = Lists.newArrayList();
        Map<Long, RelationVo<AddressVo>> map = Maps.newHashMapWithExpectedSize(relationMap.size());
        for (Long bizId : relationMap.keySet()) {
            List<AddressRelationEntity> relations = relationMap.get(bizId);
            RelationVo<AddressVo> vo = RelationVo.<AddressVo>builder()
                .bizType(request.getBizType())
                .bizId(request.getBizId())
                .relationBizType(relationBizType)
                .build();
            if (CollectionUtils.isEmpty(relations)) {
                map.put(bizId, vo);
                continue;
            }
            List<Long> ids = Lists.newArrayListWithCapacity(relations.size());
            List<AddressEntity> items = Lists.newArrayListWithCapacity(relations.size());
            for (AddressRelationEntity relation : relations) {
                Long addressId = relation.getAddressId();
                ids.add(addressId);
                items.add(entityMap.get(addressId));
            }
            vo.setIds(ids.toArray(Long[]::new));
            List<AddressVo> vos = items.stream().map(AddressConverter.INSTANCE::entity2Vo).toList();
            vo.setItems(vos);
            addressVos.addAll(vos);
            addressVos.addAll(vos);
            map.put(bizId, vo);
        }
        if (request.getFillRegionNames()) {
            this.fillRegionNames(addressVos);
        }
        return map;
    }

    @Override
    public AddressVo getAddress(AddressRequest request) {
        LambdaQueryChainWrapper<AddressEntity> wrapper = lambdaQueryWrapper()
            .eq(AddressEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(AddressEntity::getBizType, request.getBizType())
            .eq(AddressEntity::getTenantId, request.getTenantId())
            .in(CollectionUtils.isNotEmpty(request.getBizIdList()), AddressEntity::getBizId, request.getBizIdList())
            .eq(AddressEntity::getId, request.getAddressId());
        AddressVo vo = AddressConverter.INSTANCE.entity2Vo(this.findOneByWrapper(wrapper));
        if (vo != null) {
            this.fillRegionNames(Collections.singletonList(vo));
        }
        return vo;
    }

    @Override
    @TargetAddress
    public AddressEntity saveAddress(AddressForm form) {
        AddressEntity entity;
        if (form.getId() != null && form.getId() > 0) {
            entity = this.findById(form.getId());
            ObjectUtils.copyNotNullProperties(form, entity);
        } else {
            entity = AddressConverter.INSTANCE.form2Entity(form);
        }
        if (!ObjectUtils.isValidId(entity.getBizId())) {
            entity.setBizId(SecurityUtils.getUid());
        }
        this.save(entity);
        return entity;
    }

    @Override
    public void deleteAddress(AddressDeleteRequest request) {
        List<AddressEntity> list = this.findListByWrapper(this.lambdaQueryWrapper()
            .eq(AddressEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(AddressEntity::getTenantId, request.getTenantId())
            .eq(AddressEntity::getBizType, request.getBizType())
            .in(!ObjectUtils.isEmpty(request.getIds()), AddressEntity::getId, Arrays.stream(request.getIds()).toList()));

        // 批量软删除
        this.softDeleteBatch(list);
    }

    @Override
    public RelationVo<AddressVo> getRelation(RelationRequest request) {
        List<AddressRelationEntity> relationList = this.addressRelationService.findRelations(request);

        String relationBizType = request.getRelationBizType();
        if (StringUtils.isEmpty(relationBizType) && CollectionUtils.isNotEmpty(relationList)) {
            relationBizType = relationList.getFirst().getBizType();
        }

        RelationVo<AddressVo> vo = RelationVo.<AddressVo>builder()
            .bizType(request.getBizType())
            .bizId(request.getBizId())
            .relationBizType(relationBizType)
            .build();

        List<AddressEntity> entityList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(relationList)) {
            entityList.addAll(this.findByIds(relationList.stream().map(AddressRelationEntity::getAddressId).toList()));
        }
        if (CollectionUtils.isNotEmpty(entityList)) {
            vo.setIds(entityList.stream().map(AddressEntity::getId).toArray(Long[]::new));
            List<AddressVo> vos = entityList.stream().map(AddressConverter.INSTANCE::entity2Vo).toList();
            if (request.getFillRegionNames()) {
                this.fillRegionNames(vos);
            }
            if (request.getLatitude() != null
                && request.getLongitude() != null
                && CollectionUtils.isNotEmpty(vos)) {
                for (AddressVo item : vos) {
                    if (StringUtils.isNotBlank(item.getLat()) && StringUtils.isNotBlank(item.getLng())) {
                        item.setDistance(DistanceUtils.distance(request.getLatitude(), request.getLongitude(),
                            Double.parseDouble(item.getLat()), Double.parseDouble(item.getLng())));
                    }
                }
                vos = vos.stream()
                    .sorted(Comparator.comparing(AddressVo::getDistance, Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
            }
            vo.setItems(vos);
        }
        return vo;
    }

    /**
     * 填充地区名称
     */
    @Override
    public void fillRegionNames(List<AddressVo> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return;
        }
        //获取所有需要查询的地区ID
        Set<Long> regionIds = vos.stream()
            .flatMap(vo -> Stream.of(
                vo.getCountryId(),
                vo.getProvinceId(),
                vo.getCityId(),
                vo.getCountyId()
            ))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        if (regionIds.isEmpty()) {
            return;
        }
        List<RegionVo> regions = regionApi.findByIds(new ArrayList<>(regionIds));
        Map<Long, String> regionMap = regions.stream()
            .collect(Collectors.toMap(RegionVo::getId, RegionVo::getTitle));

        // 填充地区名称
        vos.forEach(vo -> {
            Optional.ofNullable(vo.getCountryId())
                .map(regionMap::get)
                .ifPresent(vo::setCountryName);

            Optional.ofNullable(vo.getProvinceId())
                .map(regionMap::get)
                .ifPresent(vo::setProvinceName);

            Optional.ofNullable(vo.getCityId())
                .map(regionMap::get)
                .ifPresent(vo::setCityName);

            Optional.ofNullable(vo.getCountyId())
                .map(regionMap::get)
                .ifPresent(vo::setCountyName);
        });
    }

    @Override
    public void solidSave(AddressEntity entity) {
        this.getMapper().insert(entity);
    }
}
