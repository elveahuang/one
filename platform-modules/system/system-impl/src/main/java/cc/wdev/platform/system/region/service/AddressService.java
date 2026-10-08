package cc.wdev.platform.system.region.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.region.domain.entity.AddressEntity;
import cc.wdev.platform.system.region.domain.form.AddressForm;
import cc.wdev.platform.system.region.domain.request.AddressDeleteRequest;
import cc.wdev.platform.system.region.domain.request.AddressRequest;
import cc.wdev.platform.system.region.domain.request.AddressSearchRequest;
import cc.wdev.platform.system.region.domain.vo.AddressVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author erden
 * @see EntityService
 */
public interface AddressService extends CachingEntityService<AddressEntity, Long> {

    /**
     * 根据业务类型获取地址分页列表
     */
    Page<AddressVo> findPageByBizType(AddressSearchRequest request);

    /**
     * 获取地址详情
     */
    AddressVo getAddress(AddressRequest request);

    /**
     * 保存地址
     */
    AddressEntity saveAddress(AddressForm form);

    /**
     * 删除地址
     */
    void deleteAddress(AddressDeleteRequest request);

    /**
     * 获取地址关联
     */
    RelationVo<AddressVo> getRelation(RelationRequest request);

    /**
     * 填充地区名称
     */
    void fillRegionNames(List<AddressVo> vos);

    /**
     * 根据业务类型获取地址列表
     */
    List<AddressVo> findListByBizType(AddressSearchRequest request);

    /**
     * 获取关联关系
     */
    Map<Long, RelationVo<AddressVo>> relationMap(RelationRequest request);

    void solidSave(AddressEntity entity);
}
