package cc.wdev.platform.system.log.api;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.service.CacheService;
import cc.wdev.platform.system.log.domain.request.SearchLogRequest;
import cc.wdev.platform.system.log.domain.request.SearchLogSaveRequest;
import cc.wdev.platform.system.log.domain.request.SearchLogSearchRequest;
import cc.wdev.platform.system.log.domain.vo.SearchLogVo;
import cc.wdev.platform.system.log.service.SearchLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.wdev.platform.system.commons.constants.SystemCacheConstants.SEARCH_HOT_LIST;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class SearchApiImpl implements SearchApi {

    private final CacheService cacheService;

    private final SearchLogService searchLogService;

    /**
     * @see SearchApi#saveSearchLog(SearchLogSaveRequest)
     */
    @Override
    public void saveSearchLog(SearchLogSaveRequest captchaLog) {
        searchLogService.saveSearchLog(captchaLog);
    }

    /**
     * @see SearchApi#getMySearchLog(SearchLogSearchRequest)
     */
    @Override
    public List<SearchLogVo> getMySearchLog(SearchLogSearchRequest request) {
        return this.searchLogService.getMySearchLog(request);
    }

    /**
     * @see SearchApi#getHotSearchLog(SearchLogSearchRequest)
     */
    @Override
    public List<SearchLogVo> getHotSearchLog(SearchLogSearchRequest request) {
        CacheKeyGenerator generator = new SimpleTenantCacheKeyGenerator(SEARCH_HOT_LIST);
        return this.cacheService.get(generator.byCode(SEARCH_HOT_LIST), _ -> this.searchLogService.getHotSearchLog(request));
    }

    /**
     * @see SearchApi#deleteMySearchLog(SearchLogRequest)
     */
    @Override
    public void deleteMySearchLog(SearchLogRequest request) {
        this.searchLogService.deleteMySearchLog(request);
    }

    /**
     * @see SearchApi#deleteMySearchLog()
     */
    @Override
    public void deleteMySearchLog() {
        this.searchLogService.deleteMySearchLog();
    }

}
