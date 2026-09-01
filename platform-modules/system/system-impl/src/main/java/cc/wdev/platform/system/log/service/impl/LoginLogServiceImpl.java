package cc.wdev.platform.system.log.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.system.log.domain.entity.LoginLogEntity;
import cc.wdev.platform.system.log.repository.LoginLogRepository;
import cc.wdev.platform.system.log.service.LoginLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author erden
 */
@Slf4j
@Service
public class LoginLogServiceImpl extends BaseCachingEntityService<LoginLogEntity, Long, LoginLogRepository> implements LoginLogService {

}
