package cc.wdev.platform.system.tag.service;

import cc.wdev.platform.commons.data.mybatis.service.EnhancedEntityService;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.commons.domain.request.SequenceRequest;
import cc.wdev.platform.system.commons.domain.vo.SequenceVo;
import cc.wdev.platform.system.tag.domain.entity.TagSequenceEntity;
import cc.wdev.platform.system.tag.repository.TagSequenceRepository;

import java.util.List;


/**
 * @author erden
 */
public interface TagSequenceService extends EntityService<TagSequenceEntity, Long>, EnhancedEntityService<TagSequenceEntity, Long, TagSequenceRepository> {

    /**
     * 保存标签个性化排序
     */
    void saveSequence(SequenceRequest request);

    /**
     * 获取标签个性化排序
     */
    SequenceVo getSequence(SequenceRequest request);

    /**
     * 查询标签个性化排序记录
     */
    List<TagSequenceEntity> findSequence(SequenceRequest request);

    /**
     * 删除标签个性化排序记录
     */
    void deleteByTagId(Long tagId);

}
