package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.ai.domain.entity.AiKbChunkEntity;

import java.util.Collection;
import java.util.List;

/**
 * @author elvea
 */
public interface AiKbChunkService extends EntityService<AiKbChunkEntity, Long> {

    /**
     * 根据知识库ID获取分片
     */
    List<AiKbChunkEntity> findByKbId(Long kbId);

    /**
     * 根据知识库ID获取分片
     */
    List<AiKbChunkEntity> findByKbId(Long kbId, int limit);

    /**
     * 根据知识条目ID获取分片
     */
    List<AiKbChunkEntity> findByKbItemId(Long kbItemId);

    /**
     * 根据知识条目ID获取分片
     */
    List<AiKbChunkEntity> findByKbItemId(Long kbItemId, int limit);

    /**
     * 按内容哈希批量查询知识库分片
     */
    List<AiKbChunkEntity> findByHashes(Long kbId, Collection<String> contentHashes);

    /**
     * 软删除知识库下全部分片
     */
    void deleteByKbId(Long kbId);

    /**
     * 软删除知识库知识条目下面所有分片
     */
    void deleteByKbItemId(Long kbItemId);

    /**
     * 判断知识库内是否已存在相同内容哈希的活跃分片
     */
    boolean existsByContentHash(Long kbId, String contentHash);

    /**
     * 统计知识库分片数
     */
    long countByKbId(Long kbId);

}
