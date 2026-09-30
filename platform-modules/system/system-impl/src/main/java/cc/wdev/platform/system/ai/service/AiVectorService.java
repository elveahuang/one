package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.system.ai.domain.vo.AiKbItemVo;
import cc.wdev.platform.system.ai.domain.vo.AiKbVo;

/**
 * 知识库向量化与检索核心服务
 *
 * @author elvea
 */
public interface AiVectorService {

    /**
     * 提交单条目向量化任务
     */
    void submitKbItemTask(Long kbId, Long kbItemId);

    /**
     * 执行单条目向量化任务
     */
    void executeKbItemTask(Long taskId);

    /**
     * 提交知识库重建任务
     */
    void submitKbTask(Long kbId);

    /**
     * 执行知识库重建任务
     */
    void executeKbTask(Long taskId);

    /**
     * 重试任务
     */
    void retryTask(Long taskId);

    /**
     * 向量化单个条目
     */
    int vectorizeItem(AiKbVo kb, AiKbItemVo item);

    /**
     * 删除知识库向量
     */
    void deleteByKb(AiKbVo kb);

    /**
     * 删除指定条目的向量
     */
    void deleteByKbItemId(AiKbVo kb, Long kbItemId);

}
