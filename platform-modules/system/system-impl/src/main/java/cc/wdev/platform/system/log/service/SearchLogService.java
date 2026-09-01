package cc.wdev.platform.system.log.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.log.domain.entity.SearchLogEntity;
import cc.wdev.platform.system.log.domain.request.SearchLogRequest;
import cc.wdev.platform.system.log.domain.request.SearchLogSaveRequest;
import cc.wdev.platform.system.log.domain.request.SearchLogSearchRequest;
import cc.wdev.platform.system.log.domain.vo.SearchLogVo;

import java.util.List;

/**
 * @author elvea
 */
public interface SearchLogService extends CachingEntityService<SearchLogEntity, Long> {

    /**
     * 保存个人搜索历史记录
     */
    void saveSearchLog(SearchLogSaveRequest request);

    /**
     * 查询个人搜索历史记录
     */
    List<SearchLogVo> getMySearchLog(SearchLogSearchRequest request);

    /**
     * 查询热搜记录
     */
    List<SearchLogVo> getHotSearchLog(SearchLogSearchRequest request);

    /**
     * 删除个人搜索历史记录
     */
    void deleteMySearchLog();

    /**
     * 删除个人搜索历史记录
     */
    void deleteMySearchLog(SearchLogRequest request);

}
