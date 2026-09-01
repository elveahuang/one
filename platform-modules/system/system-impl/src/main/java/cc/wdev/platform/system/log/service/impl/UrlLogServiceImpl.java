package cc.wdev.platform.system.log.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.system.log.domain.entity.UrlLogEntity;
import cc.wdev.platform.system.log.repository.UrlLogRepository;
import cc.wdev.platform.system.log.service.UrlLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author erden
 */
@Slf4j
@Service
public class UrlLogServiceImpl extends BaseCachingEntityService<UrlLogEntity, Long, UrlLogRepository> implements UrlLogService {

}
