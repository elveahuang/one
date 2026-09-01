package cc.wdev.platform.system.log.api;

import cc.wdev.platform.commons.core.log.domain.ApplicationLogDto;
import cc.wdev.platform.commons.core.log.domain.OperationLogDto;
import cc.wdev.platform.commons.core.log.domain.UrlLogDto;
import cc.wdev.platform.commons.extensions.captcha.domain.CaptchaLogDto;
import cc.wdev.platform.system.log.service.ApplicationLogRabbitService;
import cc.wdev.platform.system.log.service.CaptchaLogRabbitService;
import cc.wdev.platform.system.log.service.OperationLogRabbitService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class LogApiImpl implements LogApi {

    private final ApplicationLogRabbitService applicationLogRabbitService;

    private final CaptchaLogRabbitService captchaLogRabbitService;

    private final OperationLogRabbitService operationLogRabbitService;

    /**
     * @see LogApi#saveApplicationLog(ApplicationLogDto)
     */
    @Override
    public void saveApplicationLog(ApplicationLogDto dto) throws Exception {
        this.applicationLogRabbitService.send(dto);
    }

    /**
     * @see LogApi#saveCaptchaLog(CaptchaLogDto)
     */
    @Override
    public void saveCaptchaLog(CaptchaLogDto dto) throws Exception {
        this.captchaLogRabbitService.send(dto);
    }

    /**
     * @see LogApi#saveOperationLog(OperationLogDto)
     */
    @Override
    public void saveOperationLog(OperationLogDto dto) throws Exception {
        this.operationLogRabbitService.send(dto);
    }

    /**
     * @see LogApi#saveUrlLog(UrlLogDto)
     */
    @Override
    public void saveUrlLog(UrlLogDto dto) {
        log.info(dto.toString());
    }

}
