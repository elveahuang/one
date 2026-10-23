package cc.wdev.platform.system.core.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.core.domain.dto.OrganizationDto;
import cc.wdev.platform.system.core.domain.dto.OrganizationSaveDto;
import cc.wdev.platform.system.core.domain.entity.OrganizationEntity;
import cc.wdev.platform.system.core.domain.request.OrganizationSearchRequest;
import cc.wdev.platform.system.core.domain.vo.OrganizationVo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public interface OrganizationService extends CachingEntityService<OrganizationEntity, Long> {

    /**
     * 获取部门树
     */
    List<OrganizationVo> findOrganizationTree();

    /**
     * 获取部门分页列表
     */
    Page<OrganizationVo> findOrganizationPage(OrganizationSearchRequest request);

    /**
     * 获取所有可选部门（不含顶层部门）
     */
    List<OrganizationVo> findOrganizationOptions();

    /**
     * 获取部门详情
     */
    OrganizationVo findOrganizationDetails(Long id);

    /**
     * 保存部门
     */
    OrganizationDto saveOrganization(OrganizationSaveDto saveDto);

    /**
     * 删除部门
     */
    void deleteOrganization(List<Long> ids);

    /**
     * 检查部门编号是否已存在
     */
    boolean existsByCode(String code, Long id);

    /**
     * 获取顶层部门
     */
    OrganizationEntity getRootOrganization();

    /**
     * 获取默认部门
     */
    OrganizationEntity getDefaultOrganization();

}
