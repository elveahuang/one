package cc.wdev.platform.system.log.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.log.domain.entity.OperationLogEntity;
import cc.wdev.platform.system.log.domain.request.OperationLogSearchRequest;
import org.springframework.data.domain.Page;

/**
 * @author elvea
 */
public interface OperationLogService extends CachingEntityService<OperationLogEntity, Long> {

    /**
     * 查询操作日志
     */
    Page<OperationLogEntity> findOperationLogList(OperationLogSearchRequest request);

}
