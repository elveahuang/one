package cc.wdev.platform.system.dict.api;

import cc.wdev.platform.system.commons.domain.request.BizTypeRequest;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.RelationSaveRequest;
import cc.wdev.platform.system.commons.domain.request.SequenceRequest;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.commons.domain.vo.SequenceVo;
import cc.wdev.platform.system.dict.domain.request.DictRequest;
import cc.wdev.platform.system.dict.domain.request.DictSaveRequest;
import cc.wdev.platform.system.dict.domain.request.DictSearchRequest;
import cc.wdev.platform.system.dict.domain.vo.DictTypeVo;
import cc.wdev.platform.system.dict.domain.vo.DictVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
public interface DictApi {

    /**
     * 初始化字典数据
     */
    void initialize();

    /**
     * 根据code查询字典实体
     */
    DictVo findByCode(DictRequest request);

    /**
     * 保存字典实体
     */
    void saveDict(DictSaveRequest request);

    /**
     * 根据code删除字典实体
     */
    void deleteByCode(DictRequest request);

    /**
     * 获取字典类型
     */
    DictTypeVo getDictType(BizTypeRequest request);

    /**
     * 获取关联关系
     */
    Map<Long, RelationVo<DictVo>> relationMap(RelationRequest request);

    /**
     * 获取关联关系
     */
    RelationVo<DictVo> getRelation(RelationRequest request);

    /**
     * 保存关联关系
     */
    void saveRelation(RelationSaveRequest request);

    /**
     * 删除关联关系
     */
    void deleteRelation(RelationRequest request);

    /**
     * 搜索字典
     */
    Page<DictVo> search(DictSearchRequest request);

    /**
     * 搜索字典
     */
    List<DictVo> list(DictSearchRequest request);

    /**
     * 获取序列
     */
    SequenceVo getSequence(SequenceRequest request);

    /**
     * 保存序列
     */
    void saveSequence(SequenceRequest request);

    /**
     * 检查关联关系是否存在
     */
    boolean checkRelationExists(RelationRequest request);

    /**
     * 统计关联关系数量
     */
    long countRelations(RelationRequest request);

    /**
     * 重置序列为默认顺序
     */
    void resetSequence(SequenceRequest request);

    /**
     * 获取排序后的字典列表
     */
    List<DictVo> findDictWithSequence(SequenceRequest request);

}
