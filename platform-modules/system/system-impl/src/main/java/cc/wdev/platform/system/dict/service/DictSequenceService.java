package cc.wdev.platform.system.dict.service;

import cc.wdev.platform.commons.data.mybatis.service.EnhancedEntityService;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.commons.domain.request.SequenceRequest;
import cc.wdev.platform.system.commons.domain.vo.SequenceVo;
import cc.wdev.platform.system.dict.domain.entity.DictSequenceEntity;
import cc.wdev.platform.system.dict.repository.DictSequenceRepository;

import java.util.List;

/**
 * @author erden
 */
public interface DictSequenceService
    extends CachingEntityService<DictSequenceEntity, Long>, EnhancedEntityService<DictSequenceEntity, Long, DictSequenceRepository> {

    /**
     * 保存字典个性化排序
     */
    void saveSequence(SequenceRequest request);

    /**
     * 获取字典个性化排序
     */
    SequenceVo getSequence(SequenceRequest request);

    /**
     * 查询排序记录
     */
    List<DictSequenceEntity> findSequence(SequenceRequest request);
}
