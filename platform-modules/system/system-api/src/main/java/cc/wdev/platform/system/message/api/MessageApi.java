package cc.wdev.platform.system.message.api;

import cc.wdev.platform.system.message.domain.dto.CreateMessageDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@HttpExchange(url = EXCHANGE_PREFIX + "/message")
public interface MessageApi {

    // ------------------------------------------------------------------------
    // 消息相关
    // ------------------------------------------------------------------------

    /**
     * 创建消息
     */
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    Long createMessage(@RequestBody CreateMessageDto message) throws Exception;

    /**
     * 发送消息
     * 用于系统定时任务自动推送当前未发送的消息
     */
    void sendMessage() throws Exception;

    /**
     * 发送消息
     */
    void sendMessage(Long id) throws Exception;

    /**
     * 发送消息
     */
    void sendMessage(Long id, boolean force) throws Exception;

    // ------------------------------------------------------------------------
    // 数据初始化
    // ------------------------------------------------------------------------

    /**
     * 初始消息模版
     */
    void initialize();

    /**
     * 初始消息通道
     */
    void initializeMessageChannel();

    /**
     * 初始消息类型
     */
    void initializeMessageType();

    /**
     * 刷新消息模版
     */
    void initializeMessageTemplate(boolean force);

}
