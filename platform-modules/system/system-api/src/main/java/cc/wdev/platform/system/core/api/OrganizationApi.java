package cc.wdev.platform.system.core.api;

import cc.wdev.platform.system.core.domain.dto.OrganizationDto;
import cc.wdev.platform.system.core.domain.dto.OrganizationSaveDto;
import cc.wdev.platform.system.core.domain.request.OrganizationSearchRequest;
import cc.wdev.platform.system.core.domain.vo.OrganizationVo;
import org.springframework.data.domain.Page;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@HttpExchange(url = EXCHANGE_PREFIX + "/organization")
public interface OrganizationApi {

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

}
