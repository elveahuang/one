package cc.wdev.platform.commons.extensions.sensitive;

import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author elvea
 */
@Slf4j
public class SensitiveManager {

    private static final SensitiveService service = new DefaultSensitiveService();

    public static SensitiveService getService() {
        return ObjectUtils.nvl(SpringUtils.getBean(SensitiveService.class), service);
    }

}
