package cc.wdev.platform.system.log.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.system.log.domain.entity.CaptchaLogEntity;
import cc.wdev.platform.system.log.repository.CaptchaLogRepository;
import cc.wdev.platform.system.log.service.CaptchaLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author elvea
 * @see CaptchaLogService
 * @see BaseCachingEntityService
 */
@Slf4j
@AllArgsConstructor
@Service
public class CaptchaLogServiceImpl
    extends BaseCachingEntityService<CaptchaLogEntity, Long, CaptchaLogRepository>
    implements CaptchaLogService {
}
