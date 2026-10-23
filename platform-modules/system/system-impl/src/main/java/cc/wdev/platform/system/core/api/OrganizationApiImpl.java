package cc.wdev.platform.system.core.api;

import cc.wdev.platform.system.core.domain.dto.OrganizationDto;
import cc.wdev.platform.system.core.domain.dto.OrganizationSaveDto;
import cc.wdev.platform.system.core.domain.request.OrganizationSearchRequest;
import cc.wdev.platform.system.core.domain.vo.OrganizationVo;
import cc.wdev.platform.system.core.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author elvea
 * @see OrganizationApi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationApiImpl implements OrganizationApi {

    private final OrganizationService organizationService;

    /**
     * @see OrganizationApi#findOrganizationTree()
     */
    @Override
    public List<OrganizationVo> findOrganizationTree() {
        return this.organizationService.findOrganizationTree();
    }

    /**
     * @see OrganizationApi#findOrganizationPage(OrganizationSearchRequest)
     */
    @Override
    public Page<OrganizationVo> findOrganizationPage(OrganizationSearchRequest request) {
        return this.organizationService.findOrganizationPage(request);
    }

    /**
     * @see OrganizationApi#findOrganizationDetails(Long)
     */
    @Override
    public OrganizationVo findOrganizationDetails(Long id) {
        return this.organizationService.findOrganizationDetails(id);
    }

    /**
     * @see OrganizationApi#saveOrganization(OrganizationSaveDto)
     */
    @Override
    public OrganizationDto saveOrganization(OrganizationSaveDto saveDto) {
        return this.organizationService.saveOrganization(saveDto);
    }

    /**
     * @see OrganizationApi#deleteOrganization(List)
     */
    @Override
    public void deleteOrganization(List<Long> ids) {
        this.organizationService.deleteOrganization(ids);
    }

}
