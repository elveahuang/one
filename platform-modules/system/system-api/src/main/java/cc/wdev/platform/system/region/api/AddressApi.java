package cc.wdev.platform.system.region.api;

import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.region.domain.form.AddressForm;
import cc.wdev.platform.system.region.domain.request.AddressDeleteRequest;
import cc.wdev.platform.system.region.domain.request.AddressRequest;
import cc.wdev.platform.system.region.domain.request.AddressSearchRequest;
import cc.wdev.platform.system.region.domain.vo.AddressVo;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
public interface AddressApi {

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
    AddressVo saveAddress(AddressForm form);

    /**
     * 删除地址
     */
    void deleteAddress(AddressDeleteRequest request);

    /**
     * 获取地址关联关系
     */
    RelationVo<AddressVo> getRelation(RelationRequest request);

    /**
     * 获取关联关系
     */
    Map<Long, RelationVo<AddressVo>> relationMap(RelationRequest request);

    /**
     * 保存地址关联关系
     */
    void saveRelation(RelationSaveRequest request);

    /**
     * 删除地址关联关系
     */
    void deleteRelation(RelationRequest request);

    /**
     * 检查地址关联关系
     */
    boolean checkRelation(RelationRequest request);

    /**
     * 统计地址关联关系
     */
    long countRelation(RelationRequest request);

    /**
     * 业务类型获取地址列表
     */
    List<AddressVo> findListByBizType(@Valid AddressSearchRequest request);

    void solidSave(AddressForm form);

    /**
     * 批量获取地址信息
     */
    Map<Long, AddressVo> batchAddress(Collection<Long> addressIds);

    /**
     * 根据地址ID查询关联的业务ID列表
     */
    List<Long> findBizIdsByAddressId(Long addressId, String relationBizType);
}
