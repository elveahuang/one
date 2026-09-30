package cc.wdev.platform.system.log.api;

import cc.wdev.platform.commons.core.log.domain.ApplicationLogDto;
import cc.wdev.platform.commons.core.log.domain.OperationLogDto;
import cc.wdev.platform.commons.core.log.domain.UrlLogDto;
import cc.wdev.platform.commons.extensions.captcha.domain.CaptchaLogDto;

/**
 * @author elvea
 */
public interface LogApi {

    /**
     * 保存应用日志
     */
    void saveApplicationLog(ApplicationLogDto dto) throws Exception;

    /**
     * 保存操作日志
     */
    void saveOperationLog(OperationLogDto dto) throws Exception;

    /**
     * 保存验证码日志
     */
    void saveCaptchaLog(CaptchaLogDto dto) throws Exception;

    /**
     * 保存链接日志
     */
    void saveUrlLog(UrlLogDto dto);

}
