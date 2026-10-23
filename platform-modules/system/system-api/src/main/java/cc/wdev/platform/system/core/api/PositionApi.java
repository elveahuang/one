package cc.wdev.platform.system.core.api;

import cc.wdev.platform.system.core.domain.dto.PositionDto;
import cc.wdev.platform.system.core.domain.dto.PositionSaveDto;
import cc.wdev.platform.system.core.domain.request.PositionSearchRequest;
import cc.wdev.platform.system.core.domain.vo.PositionVo;
import org.springframework.data.domain.Page;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@HttpExchange(url = EXCHANGE_PREFIX + "/position")
public interface PositionApi {

    /**
     * 获取岗位树
     */
    List<PositionVo> findPositionTree();

    /**
     * 获取岗位分页列表
     */
    Page<PositionVo> findPositionPage(PositionSearchRequest request);

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

}
