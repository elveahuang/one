package cc.wdev.platform.commons.core.log.store;

import cc.wdev.platform.commons.core.log.domain.ApplicationLogDto;
import cc.wdev.platform.commons.core.log.domain.OperationLogDto;
import cc.wdev.platform.commons.core.log.domain.UrlLogDto;

/**
 * @author elvea
 */
public interface LogStore {

    /**
     * 保存系统日志
     */
    void saveApplicationLog(ApplicationLogDto dto) throws Exception;

    /**
     * 保存操作日志
     */
    void saveOperationLog(OperationLogDto dto) throws Exception;

    /**
     * 保存链接日志
     */
    void saveUrlLog(UrlLogDto dto) throws Exception;

}
