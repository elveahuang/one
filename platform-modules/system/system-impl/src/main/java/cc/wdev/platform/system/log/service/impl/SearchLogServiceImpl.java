package cc.wdev.platform.system.log.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.log.domain.entity.SearchLogEntity;
import cc.wdev.platform.system.log.domain.request.SearchLogRequest;
import cc.wdev.platform.system.log.domain.request.SearchLogSaveRequest;
import cc.wdev.platform.system.log.domain.request.SearchLogSearchRequest;
import cc.wdev.platform.system.log.domain.vo.SearchLogVo;
import cc.wdev.platform.system.log.repository.SearchLogRepository;
import cc.wdev.platform.system.log.service.SearchLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class SearchLogServiceImpl extends BaseCachingEntityService<SearchLogEntity, Long, SearchLogRepository> implements SearchLogService {

    /**
     * @see SearchLogService#saveSearchLog(SearchLogSaveRequest)
     */
    @Override
    public void saveSearchLog(SearchLogSaveRequest request) {
        SearchLogEntity entity = SearchLogEntity.builder()
            .searchKey(StringUtils.nvl(request.getSearchKey()))
            .userId(SecurityUtils.getUid())
            .build();
        this.save(entity);
    }

    /**
     * @see SearchLogService#getMySearchLog(SearchLogSearchRequest)
     */
    @Override
    public List<SearchLogVo> getMySearchLog(SearchLogSearchRequest request) {
        request.setUserId(SecurityUtils.getUid());
        return this.mapper.getSearchLogList(getMyBatisPlusPage(request), request);
    }

    /**
     * @see SearchLogService#getHotSearchLog(SearchLogSearchRequest)
     */
    @Override
    public List<SearchLogVo> getHotSearchLog(SearchLogSearchRequest request) {
        request.setUserId(SecurityUtils.getUid());
        request.setSince(LocalDateTime.now().minusDays(1));
        return this.mapper.getSearchLogHotList(getMyBatisPlusPage(request), request);
    }

    /**
     * @see SearchLogService#deleteMySearchLog()
     */
    @Override
    public void deleteMySearchLog() {
        List<SearchLogEntity> entities = this.lambdaQueryWrapper()
            .eq(SearchLogEntity::getUserId, SecurityUtils.getUid())
            .list();
        this.deleteBatch(entities);
    }

    /**
     * @see SearchLogService#deleteMySearchLog(SearchLogRequest)
     */
    @Override
    public void deleteMySearchLog(SearchLogRequest request) {
        List<SearchLogEntity> entities = this.lambdaQueryWrapper()
            .eq(SearchLogEntity::getUserId, SecurityUtils.getUid())
            .eq(SearchLogEntity::getSearchKey, request.getSearchKey())
            .list();
        this.deleteBatch(entities);
    }

}
