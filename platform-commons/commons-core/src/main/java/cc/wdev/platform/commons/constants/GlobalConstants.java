package cc.wdev.platform.commons.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;

/**
 * @author elvea
 */
public interface GlobalConstants {

    /**
     * 全局应用名
     */
    String NAME = "App";

    /**
     * 全局版本号
     */
    String VERSION = "26.2.0";

    /**
     * 全局默认编码
     */
    Charset UTF8 = StandardCharsets.UTF_8;

    /**
     * 全局默认编码
     */
    String ENCODING = StandardCharsets.UTF_8.displayName();

    /**
     * 默认语言
     */
    Locale DEFAULT_LOCALE = Locale.getDefault();

    /**
     * 默认缓存周期为1个小时
     */
    Duration DEFAULT_CACHE_DURATION = Duration.ofHours(1);

    /**
     * 批处理记录数
     */
    int BATCH_SIZE = 1000;

    /**
     * 缓存批处理记录数
     */
    int MAX_BATCH_CACHE_KEY_SIZE = 20;

    /**
     * 默认分隔符
     */
    String DELIMITER = "~|~";

    /**
     * 默认分隔符
     */
    String STR_DELIMITER = ",";

    /**
     * 多语言文本前缀
     */
    String I18N_LABEL_GROUP = "common";

    /**
     * 默认的邮件服务端口
     */
    int DEFAULT_SMTP_PORT = 25;

    /**
     *
     */
    String REQUEST_ID_KEY = "x-request-id";

    /**
     *
     */
    String TENANT_ID_KEY = "x-tenant-id";
    /**
     *
     */
    String TENANT_ROOT_IND_KEY = "x-tenant-root-ind";

    /**
     *
     */
    String TENANT_CODE_KEY = "x-tenant-code";

    /**
     * 基础包名
     */
    String GLOABL_BASE_PACKAGE = "cc.wdev";

    /**
     *
     */
    String CONTENT_REQUEST_ID_KEY = "requestId";

    String CONTENT_TENANT_ID_KEY = "tenantId";

}
