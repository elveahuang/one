package cc.wdev.platform.system.core.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.core.domain.entity.TenantEntity;
import cc.wdev.platform.system.core.domain.form.AddTenantForm;
import cc.wdev.platform.system.core.domain.form.BindTenantPackageForm;
import cc.wdev.platform.system.core.domain.form.EditTenantForm;
import cc.wdev.platform.system.core.domain.request.TenantSearchRequest;
import cc.wdev.platform.system.core.domain.vo.TenantVo;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author erden
 */
public interface TenantService extends CachingEntityService<TenantEntity, Long> {

    /**
     * 根据租户编码查找租户
     */
    TenantEntity findByCode(String code);

    /**
     * 获取租户列表
     * <p>
     * 这里需要特别注意，列表不包含顶层租户，顶层租户不允许修改
     */
    Page<TenantVo> findTenantPage(TenantSearchRequest request);

    /**
     * 添加租户信息
     *
     * @param form 表单参数
     */
    void addTenant(AddTenantForm form);

    /**
     * 编辑租户信息
     *
     * @param form 表单参数
     */
    void editTenant(EditTenantForm form);

    /**
     * 获取租户详细信息
     *
     * @param tenantId 租户ID
     */
    TenantVo findTenantById(Long tenantId);

    /**
     * 获取租户详细信息
     *
     * @param domain 租户域名
     */
    TenantEntity findByDomain(String domain);

    /**
     * 删除租户
     *
     * @param tenantId 租户ID
     */
    void deleteTenant(Long tenantId);

    /**
     * 批量删除租户
     *
     * @param tenantIds 租户ID数组
     */
    void batchDeleteTenant(List<Long> tenantIds);

    /**
     * 绑定租户套餐
     *
     * @param form 表单参数
     */
    void bindPackage(BindTenantPackageForm form);

    /**
     * 同步租户套餐
     *
     * @param tenantId 租户ID
     */
    void syncPackage(Long tenantId);

    /**
     * 启用租户
     *
     * @param tenantId 租户ID
     */
    void enableTenant(Long tenantId);

    /**
     * 禁用租户
     *
     * @param tenantId 租户ID
     */
    void disableTenant(Long tenantId);

    /**
     * 更新过期时间
     *
     * @param tenantId       租户ID
     * @param expirationDate 过期时间
     */
    void updateExpirationDate(Long tenantId, LocalDateTime expirationDate);

    /**
     * 查询所有租户
     */
    List<TenantEntity> findAll();

    /**
     * 获取单位时间内注册时租户的数量
     */
    long getAllTenantCount(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 租户列表
     */
    List<TenantEntity> filter();
}
