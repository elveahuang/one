package cc.wdev.platform.system.region.api;

import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.region.domain.convert.AddressConverter;
import cc.wdev.platform.system.region.domain.entity.AddressEntity;
import cc.wdev.platform.system.region.domain.form.AddressForm;
import cc.wdev.platform.system.region.domain.request.AddressDeleteRequest;
import cc.wdev.platform.system.region.domain.request.AddressRequest;
import cc.wdev.platform.system.region.domain.request.AddressSearchRequest;
import cc.wdev.platform.system.region.domain.vo.AddressVo;
import cc.wdev.platform.system.region.service.AddressRelationService;
import cc.wdev.platform.system.region.service.AddressService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddressApiImpl implements AddressApi {

    private final AddressService addressService;

    private final AddressRelationService addressRelationService;

    /**
     * @see AddressApi#findPageByBizType(AddressSearchRequest)
     */
    @Override
    public Page<AddressVo> findPageByBizType(AddressSearchRequest request) {
        return addressService.findPageByBizType(request);
    }

    /**
     * @see AddressApi#findListByBizType(AddressSearchRequest)
     */
    @Override
    public List<AddressVo> findListByBizType(AddressSearchRequest request) {
        return addressService.findListByBizType(request);
    }

    @Override
    public void solidSave(AddressForm form) {
        AddressEntity entity = AddressConverter.INSTANCE.form2Entity(form);
        entity.setId(form.getId());
        addressService.solidSave(entity);
    }

    @Override
    public Map<Long, AddressVo> batchAddress(Collection<Long> addressIds) {
        List<AddressEntity> entities = addressService.findCacheByIds(addressIds);
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyMap();
        }
        List<AddressVo> vos = Lists.newArrayListWithCapacity(entities.size());
        for (AddressEntity entity : entities) {
            AddressVo vo = AddressConverter.INSTANCE.entity2Vo(entity);
            vos.add(vo);
        }
        addressService.fillRegionNames(vos);
        return vos.stream().collect(Collectors.toMap(AddressVo::getId, Function.identity(), (e, _) -> e));
    }

    /**
     * @see AddressApi#getAddress(AddressRequest)
     */
    @Override
    public AddressVo getAddress(AddressRequest request) {
        return addressService.getAddress(request);
    }

    /**
     * @see AddressApi#saveAddress(AddressForm)
     */
    @Override
    public AddressVo saveAddress(AddressForm form) {
        AddressEntity entity = addressService.saveAddress(form);
        return AddressConverter.INSTANCE.entity2Vo(entity);
    }

    /**
     * @see AddressApi#deleteAddress(AddressDeleteRequest)
     */
    @Override
    public void deleteAddress(AddressDeleteRequest request) {
        // 查询有没有关联关系，存在关联关系不允许删除
        Boolean hasRelated = addressRelationService.hasRelated(request);
        if (hasRelated) {
            throw new ServiceException(ResponseCodeEnum.ALREADY_EXISTS_DELETE_ERROR);
        }
        addressService.deleteAddress(request);
    }

    /**
     * @see AddressApi#getRelation(RelationRequest)
     */
    @Override
    public RelationVo<AddressVo> getRelation(RelationRequest request) {
        return addressService.getRelation(request);
    }

    @Override
    public Map<Long, RelationVo<AddressVo>> relationMap(RelationRequest request) {
        return addressService.relationMap(request);
    }

    /**
     * @see AddressApi#saveRelation(RelationSaveRequest)
     */
    @Override
    public void saveRelation(RelationSaveRequest request) {
        addressRelationService.saveRelation(request);
    }

    /**
     * @see AddressApi#deleteRelation(RelationRequest)
     */
    @Override
    public void deleteRelation(RelationRequest request) {
        addressRelationService.deleteRelation(request);
    }

    /**
     * @see AddressApi#checkRelation(RelationRequest)
     */
    @Override
    public boolean checkRelation(RelationRequest request) {
        return false;
    }

    /**
     * @see AddressApi#countRelation(RelationRequest)
     */
    @Override
    public long countRelation(RelationRequest request) {
        return 0;
    }

    @Override
    public List<Long> findBizIdsByAddressId(Long addressId, String relationBizType) {
        return addressRelationService.findBizIdsByAddressId(addressId, relationBizType);
    }

}
