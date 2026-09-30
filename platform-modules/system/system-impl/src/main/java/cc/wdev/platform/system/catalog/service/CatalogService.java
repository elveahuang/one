package cc.wdev.platform.system.catalog.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.catalog.domain.entity.CatalogEntity;
import cc.wdev.platform.system.catalog.domain.request.CatalogDeleteRequest;
import cc.wdev.platform.system.catalog.domain.request.CatalogRequest;
import cc.wdev.platform.system.catalog.domain.request.CatalogSaveRequest;
import cc.wdev.platform.system.catalog.domain.vo.CatalogVo;
import org.springframework.data.domain.Page;

/**
 * @author elvea
 */
public interface CatalogService extends CachingEntityService<CatalogEntity, Long> {

    /**
     * 保存目录
     */
    CatalogVo saveCatalog(CatalogSaveRequest saveDto);

    /**
     * 删除目录
     */
    void deleteCatalog(CatalogDeleteRequest deleteDto);

    /**
     * 获取顶层目录
     */
    CatalogVo getRootCatalog(String bizType, Long bizId);

    /**
     * 获取目录
     */
    CatalogVo getCatalog(Long id);

    /**
     * 获取目录列表
     */
    Page<CatalogVo> getCatalogList(CatalogRequest request);

}
