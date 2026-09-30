package cc.wdev.platform.system.log.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.system.log.domain.entity.ApplicationLogEntity;
import cc.wdev.platform.system.log.repository.ApplicationLogRepository;
import cc.wdev.platform.system.log.service.ApplicationLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author elvea
 * @see ApplicationLogService
 * @see BaseCachingEntityService
 */
@Slf4j
@AllArgsConstructor
@Service
public class ApplicationLogServiceImpl
    extends BaseCachingEntityService<ApplicationLogEntity, Long, ApplicationLogRepository>
    implements ApplicationLogService {
}
