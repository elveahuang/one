package cc.wdev.platform.system.log.api;

import cc.wdev.platform.system.log.domain.request.SearchLogRequest;
import cc.wdev.platform.system.log.domain.request.SearchLogSaveRequest;
import cc.wdev.platform.system.log.domain.request.SearchLogSearchRequest;
import cc.wdev.platform.system.log.domain.vo.SearchLogVo;

import java.util.List;

/**
 * @author elvea
 */
public interface SearchApi {

    /**
     * 保存个人搜索记录
     */
    void saveSearchLog(SearchLogSaveRequest request);

    /**
     * 获取搜索历史
     */
    List<SearchLogVo> getMySearchLog(SearchLogSearchRequest request);

    /**
     * 获取搜索热榜
     */
    List<SearchLogVo> getHotSearchLog(SearchLogSearchRequest request);

    /**
     * 删除个人搜索历史记录
     */
    void deleteMySearchLog(SearchLogRequest request);

    /**
     * 删除个人搜索历史记录
     */
    void deleteMySearchLog();

}
