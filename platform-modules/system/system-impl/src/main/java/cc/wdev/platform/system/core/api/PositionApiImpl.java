package cc.wdev.platform.system.core.api;

import cc.wdev.platform.system.core.domain.dto.PositionDto;
import cc.wdev.platform.system.core.domain.dto.PositionSaveDto;
import cc.wdev.platform.system.core.domain.request.PositionSearchRequest;
import cc.wdev.platform.system.core.domain.vo.PositionVo;
import cc.wdev.platform.system.core.service.PositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author elvea
 * @see PositionApi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionApiImpl implements PositionApi {

    private final PositionService positionService;

    /**
     * @see PositionApi#findPositionTree()
     */
    @Override
    public List<PositionVo> findPositionTree() {
        return this.positionService.findPositionTree();
    }

    /**
     * @see PositionApi#findPositionPage(PositionSearchRequest)
     */
    @Override
    public Page<PositionVo> findPositionPage(PositionSearchRequest request) {
        return this.positionService.findPositionPage(request);
    }

    /**
     * @see PositionApi#findPositionDetails(Long)
     */
    @Override
    public PositionVo findPositionDetails(Long id) {
        return this.positionService.findPositionDetails(id);
    }

    /**
     * @see PositionApi#savePosition(PositionSaveDto)
     */
    @Override
    public PositionDto savePosition(PositionSaveDto saveDto) {
        return this.positionService.savePosition(saveDto);
    }

    /**
     * @see PositionApi#deletePosition(List)
     */
    @Override
    public void deletePosition(List<Long> ids) {
        this.positionService.deletePosition(ids);
    }

}
