package cc.wdev.platform.system.commons.constants;

/**
 * @author elvea
 */
public interface SystemRabbitConstants {

    // --------------------------------------------------------------------------------
    // 通用
    // --------------------------------------------------------------------------------

    String COMMON_DEAD_LETTER_EXCHANGE = "COMMON_DEAD_LETTER_EXCHANGE";

    String COMMON_DEAD_LETTER_PREFIX = "dead-letter.";

    String ARG_MESSAGE_TTL = "x-message-ttl";

    // --------------------------------------------------------------------------------
    // 基础队列
    // --------------------------------------------------------------------------------

    String LOGIN_SESSION = "LOGIN_SESSION_QUEUE";

    String CAPTCHA_LOG_QUEUE = "CAPTCHA_LOG_QUEUE";

    String APPLICATION_LOG_QUEUE = "APPLICATION_LOG_QUEUE";

    String OPERATION_LOG_QUEUE = "OPERATION_LOG_QUEUE";

    String MESSAGE_QUEUE = "MESSAGE_QUEUE";

    String ACCOUNT_SYNC_QUEUE = "ACCOUNT_SYNC_QUEUE";

    String ADDRESS_SYNC_QUEUE = "ADDRESS_SYNC_QUEUE";

    String MULTIPLE_LOGIN_QUEUE = "MULTIPLE_LOGIN_QUEUE";

    String MULTIPLE_LOGIN_QUEUE_EXCHANGE = "MULTIPLE_LOGIN_QUEUE_EXCHANGE";

    // --------------------------------------------------------------------------------
    // 延迟队列
    // --------------------------------------------------------------------------------

    // 会员订单超时处理队列

    String VIP_ORDER_QUEUE = "VIP_ORDER_QUEUE";
    String VIP_ORDER_EXCHANGE = "VIP_ORDER_EXCHANGE";
    String VIP_ORDER_ROUTING_KEY = "VIP_ORDER_ROUTING_KEY";
    String VIP_ORDER_TTL_EXCHANGE = "VIP_ORDER_TTL_EXCHANGE";
    String VIP_ORDER_TTL_QUEUE = "VIP_ORDER_TTL_QUEUE";
    String VIP_ORDER_TTL_ROUTING_KEY = "VIP_ORDER_TTL_ROUTING_KEY";

}
