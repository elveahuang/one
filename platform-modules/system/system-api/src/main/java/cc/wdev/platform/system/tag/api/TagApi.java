package cc.wdev.platform.system.tag.api;

import cc.wdev.platform.system.commons.domain.request.BizTypeRequest;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.commons.domain.request.SequenceRequest;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.commons.domain.vo.SequenceVo;
import cc.wdev.platform.system.tag.domain.request.*;
import cc.wdev.platform.system.tag.domain.vo.TagTypeVo;
import cc.wdev.platform.system.tag.domain.vo.TagVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
public interface TagApi {

    /**
     * 初始化字典数据
     */
    void initialize();

    /**
     * 获取标签
     * 请求参数必须包含标签类型，标签ID，租户ID
     */
    TagVo getTag(TagRequest request);

    /**
     * 保存标签
     */
    TagVo saveTag(TagSaveRequest form);

    /**
     * 删除标签
     */
    void deleteTag(TagDeleteRequest request);

    /**
     * 获取标签类型
     */
    TagTypeVo getTagType(BizTypeRequest request);

    /**
     * 获取关联关系
     */
    RelationVo<TagVo> getRelation(RelationRequest request);

    /**
     * 获取关联关系
     */
    Map<Long, RelationVo<TagVo>> relationMap(RelationRequest request);

    /**
     * 保存关联关系
     */
    void saveRelation(RelationSaveRequest request);

    /**
     * 删除关联关系
     */
    void deleteRelation(RelationRequest request);

    /**
     * 检查关联关系是否存在
     */
    boolean checkRelationExists(RelationRequest request);

    /**
     * 统计关联关系数量
     */
    long countRelations(RelationRequest request);

    /**
     * 搜索标签
     */
    Page<TagVo> search(TagSearchRequest request);

    /**
     * 搜索字典
     */
    List<TagVo> list(TagSearchRequest request);

    /**
     * 获取序列
     */
    SequenceVo getSequence(SequenceRequest request);

    /**
     * 保存序列
     */
    void saveSequence(SequenceRequest request);

    /**
     * 重置序列为默认顺序
     */
    void resetSequence(SequenceRequest request);

    /**
     * 排序标签
     */
    void sortTag(TagSortRequest request);

    /**
     * 检查用户招聘标签
     */
    Boolean checkTitle(TagTitleCheckRequest request);
}
