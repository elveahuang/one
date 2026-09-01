package cc.wdev.platform.commons.core.log.store;

import cc.wdev.platform.commons.core.log.config.LogConfig;
import cc.wdev.platform.commons.core.log.domain.ApplicationLogDto;
import cc.wdev.platform.commons.core.log.domain.OperationLogDto;
import cc.wdev.platform.commons.core.log.domain.UrlLogDto;
import lombok.extern.slf4j.Slf4j;

/**
 * @author elvea
 */
@Slf4j
public record DefaultLogStore(LogConfig config) implements LogStore {

    /**
     * @see LogStore#saveApplicationLog(ApplicationLogDto)
     */
    @Override
    public void saveApplicationLog(ApplicationLogDto dto) {
        log.info(dto.toString());
    }

    /**
     * @see LogStore#saveOperationLog(OperationLogDto)
     */
    @Override
    public void saveOperationLog(OperationLogDto dto) {
        log.info(dto.toString());
    }

    /**
     * @see LogStore#saveUrlLog(UrlLogDto)
     */
    @Override
    public void saveUrlLog(UrlLogDto dto) {
        log.info(dto.toString());
    }

}
