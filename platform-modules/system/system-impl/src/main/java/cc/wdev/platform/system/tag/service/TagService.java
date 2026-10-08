package cc.wdev.platform.system.tag.service;

import cc.wdev.platform.commons.data.mybatis.service.EnhancedEntityService;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.commons.domain.request.BizTypeRequest;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.SequenceRequest;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.tag.domain.entity.TagEntity;
import cc.wdev.platform.system.tag.domain.request.*;
import cc.wdev.platform.system.tag.domain.vo.TagTypeVo;
import cc.wdev.platform.system.tag.domain.vo.TagVo;
import cc.wdev.platform.system.tag.repository.TagRepository;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author irving
 */
public interface TagService extends CachingEntityService<TagEntity, Long>, EnhancedEntityService<TagEntity, Long, TagRepository> {

    /**
     * 保存标签项
     */
    TagVo saveTag(TagSaveRequest form);

    /**
     * 删除标签
     */
    void deleteTag(TagDeleteRequest request);

    /**
     * 根据id获取标签项
     */
    TagEntity findById(Long id);

    /**
     * 根据title获取标签项
     */
    TagEntity findByTitle(String title);

    /**
     * 根据业务类型和标题获取标签项
     */
    TagEntity findByBizTypeAndTitle(String bizType, String title);

    /**
     * 根据id删除标签项
     */
    void deleteById(Long id);

    /**
     * 根据title删除标签项
     */
    void deleteByTitle(String title);

    /**
     * 根据title更新标签项
     */
    void updateByTitle(TagSaveRequest form);

    /**
     * 获取标签类型
     */
    TagTypeVo getTagType(BizTypeRequest request);

    /**
     * 获取标签关联
     */
    RelationVo<TagVo> getRelation(RelationRequest request);

    /**
     * 搜索指定标签类型下面的标签
     */
    Page<TagVo> search(TagSearchRequest request);

    /**
     * 搜索指定标签类型下面的标签
     */
    List<TagVo> list(TagSearchRequest request);

    /**
     * 搜索指定标签类型下面的标签
     */
    Page<TagEntity> findByPage(TagSearchRequest request);

    /**
     * 检查标题是否可用
     */
    Boolean checkTitle(Long id, String title);

    /**
     * 检查标题是否可用
     */
    Boolean checkTitle(TagTitleCheckRequest request);

    /**
     * 根据标签类型id删除相关联的标签
     */
    void deleteByType(String bizType);

    /**
     * 检查关联关系是否存在
     */
    Boolean checkRelationExists(RelationRequest request);

    /**
     * 统计关联关系数量
     */
    Integer countRelations(RelationRequest request);


    /**
     * 重置序列，恢复默认顺序
     */
    void resetSequence(SequenceRequest request);

    /**
     * 获取标签项
     */
    TagVo getTag(TagRequest request);

    /**
     * 获取关联关系
     */
    Map<Long, RelationVo<TagVo>> relationMap(RelationRequest request);

    /**
     * 排序标签项
     */
    void sort(TagSortRequest request);
}
