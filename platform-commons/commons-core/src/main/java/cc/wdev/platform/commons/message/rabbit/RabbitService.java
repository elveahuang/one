package cc.wdev.platform.commons.message.rabbit;

/**
 * @author elvea
 */
public interface RabbitService<T> {

    /**
     * 发送消息
     *
     * @param body 消息内容
     */
    void send(T body) throws Exception;

    /**
     * 发送消息
     *
     * @param routingKey 队列名称
     * @param body       消息内容
     */
    void send(String routingKey, T body) throws Exception;

    /**
     * 发送消息
     *
     * @param exchange   交换机
     * @param routingKey 队列名称
     * @param body       消息内容
     */
    void send(String exchange, String routingKey, T body) throws Exception;

    /**
     * 事务内安全发送：存在活动事务时延迟到提交后发送（避免消费者读到未提交数据），
     * 无事务时等同 {@link #send(String, Object)}
     *
     * @param routingKey 队列名称
     * @param body       消息内容
     */
    default void sendAfterCommit(String routingKey, T body) throws Exception {
        this.send(routingKey, body);
    }

    default String getExchange() {
        return "";
    }

}
