package cc.wdev.platform.system.catalog.api;

import cc.wdev.platform.system.catalog.domain.request.CatalogDeleteRequest;
import cc.wdev.platform.system.catalog.domain.request.CatalogRequest;
import cc.wdev.platform.system.catalog.domain.request.CatalogSaveRequest;
import cc.wdev.platform.system.catalog.domain.vo.CatalogVo;
import org.springframework.data.domain.Page;

/**
 * @author elvea
 */
public interface CatalogApi {

    CatalogVo saveCatalog(CatalogSaveRequest saveDto);

    void deleteCatalog(CatalogDeleteRequest deleteDto);

    CatalogVo getCatalog(Long id);

    CatalogVo getRootCatalog(String bizType, Long bizId);

    Page<CatalogVo> findCatalogList(CatalogRequest request);
}
