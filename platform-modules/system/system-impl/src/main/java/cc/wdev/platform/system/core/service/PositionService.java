package cc.wdev.platform.system.core.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.core.domain.dto.PositionDto;
import cc.wdev.platform.system.core.domain.dto.PositionSaveDto;
import cc.wdev.platform.system.core.domain.entity.PositionEntity;
import cc.wdev.platform.system.core.domain.request.PositionSearchRequest;
import cc.wdev.platform.system.core.domain.vo.PositionVo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public interface PositionService extends CachingEntityService<PositionEntity, Long> {

    /**
     * 获取岗位树
     */
    List<PositionVo> findPositionTree();

    /**
     * 获取岗位分页列表
     */
    Page<PositionVo> findPositionPage(PositionSearchRequest request);

    /**
     * 获取所有可选岗位（不含顶层岗位）
     */
    List<PositionVo> findPositionOptions();

    /**
     * 获取岗位详情
     */
    PositionVo findPositionDetails(Long id);

    /**
     * 保存岗位
     */
    PositionDto savePosition(PositionSaveDto saveDto);

    /**
     * 删除岗位
     */
    void deletePosition(List<Long> ids);

    /**
     * 检查岗位编号是否已存在
     */
    boolean existsByCode(String code, Long id);

    /**
     * 获取顶层岗位
     */
    PositionEntity getRootPosition();

    /**
     * 获取默认岗位
     */
    PositionEntity getDefaultPosition();

}
