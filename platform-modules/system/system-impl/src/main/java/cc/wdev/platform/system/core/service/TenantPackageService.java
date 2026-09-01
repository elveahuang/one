package cc.wdev.platform.system.core.service;

import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.core.domain.entity.PackageEntity;
import cc.wdev.platform.system.core.domain.form.AddTenantPackageForm;
import cc.wdev.platform.system.core.domain.form.EditTenantPackageAuthorityForm;
import cc.wdev.platform.system.core.domain.form.EditTenantPackageForm;
import cc.wdev.platform.system.core.domain.request.TenantPackageSearchRequest;
import cc.wdev.platform.system.core.domain.vo.TenantPackageVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface TenantPackageService extends EntityService<PackageEntity, Long> {


    /**
     * 获取租户套餐列表
     *
     * @param request 请求参数
     * @return {@link Page }<{@link TenantPackageVo }>
     */
    Page<TenantPackageVo> findPackagePage(TenantPackageSearchRequest request);

    /**
     * 添加套餐
     *
     * @param form 表单参数
     */
    void addPackage(AddTenantPackageForm form);

    /**
     * 编辑套餐
     *
     * @param form 表单参数
     */
    void editPackage(EditTenantPackageForm form);

    /**
     * 获取套餐详情
     *
     * @param id 套餐ID
     * @return {@link TenantPackageVo }
     */
    TenantPackageVo findPackageById(Long id);

    /**
     * 编辑套餐权限
     *
     * @param form 表单参数
     */
    void editPackageAuthority(EditTenantPackageAuthorityForm form);

    /**
     * 删除租户套餐
     *
     * @param packageId 套餐ID
     */
    void deletePackage(Long packageId);

    /**
     * 批量删除租户套餐
     *
     * @param packageIds 套餐ID数组
     */
    void batchDeletePackage(List<Long> packageIds);

    /**
     * 获取套餐权限ID列表
     *
     * @param packageId 套餐ID
     * @return {@link Set }<{@link Long }>
     */
    Set<Long> findAuthorityIdsByPackageId(Long packageId);

    /**
     * 获取租户套餐列表
     *
     * @return {@link List }<{@link TenantPackageVo }>
     */
    List<TenantPackageVo> list();
}
