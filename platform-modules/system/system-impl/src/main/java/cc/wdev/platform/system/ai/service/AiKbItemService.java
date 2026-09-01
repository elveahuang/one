package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.ai.domain.entity.AiKbItemEntity;
import cc.wdev.platform.system.ai.domain.request.AiKbItemResolveRequest;
import cc.wdev.platform.system.ai.domain.request.AiKbItemSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiKbItemVo;
import cc.wdev.platform.system.commons.domain.request.GetRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
public interface AiKbItemService extends EntityService<AiKbItemEntity, Long> {

    /**
     * 查询单个知识条目
     */
    AiKbItemVo getKbItem(GetRequest request);

    /**
     * 查询单个知识条目
     */
    AiKbItemVo resolveKbItem(AiKbItemResolveRequest request);

    /**
     * 分页查询条目
     */
    Page<AiKbItemEntity> findByPage(AiKbItemSearchRequest request);

    /**
     * 根据知识库ID获取条目
     */
    List<AiKbItemEntity> findByKbId(Long kbId);

    /**
     * 根据知识库ID获取条目
     */
    List<AiKbItemEntity> findByKbId(Long kbId, int limit);

    /**
     * 标记向量化完成
     */
    void markCompleted(Long id);

    /**
     * 标记向量化失败
     */
    void markFailed(Long id, String errorMsg);

    /**
     * 软删除知识库下全部条目
     */
    void deleteByKbId(Long kbId);

    /**
     * 统计知识库条目数，统计时会过滤已经软删除的记录
     */
    long countByKbId(Long kbId);

    /**
     * 统计已向量化条目数
     */
    long countVectorizedByKbId(Long kbId);

    /**
     * 统计待处理条目数
     */
    long countPendingByKbId(Long kbId);

    /**
     * 统计失败条目数
     */
    long countFailedByKbId(Long kbId);

    /**
     * 判断知识库内是否已存在相同内容哈希的活跃条目
     */
    boolean existsByContentHash(Long kbId, String contentHash);

    /**
     * 按业务类型统计知识库条目数
     */
    Map<String, Long> groupCountByBizType(Long kbId);

}
