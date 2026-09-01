package cc.wdev.platform.system.ai.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.ai.domain.entity.AiKbEntity;
import cc.wdev.platform.system.ai.domain.request.AiKbSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiKbSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiKbVo;
import cc.wdev.platform.system.commons.domain.request.GetRequest;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author elvea
 */
public interface AiKbService extends CachingEntityService<AiKbEntity, Long> {

    /**
     * 根据编号或者ID获取知识库，编号优先级高
     */
    AiKbVo getKb(GetRequest request);

    /**
     * 保存知识库
     */
    AiKbEntity saveKb(AiKbSaveRequest request);

    /**
     * 获取可用知识库
     */
    List<AiKbEntity> getKbs();

    /**
     * 分页查询知识库
     */
    Page<AiKbEntity> findByPage(AiKbSearchRequest request);

    /**
     * 按集合名查询知识库实体
     */
    AiKbEntity findByCollectionName(String collectionName);

}
