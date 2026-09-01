package cc.wdev.platform.system.dict.service;

import cc.wdev.platform.commons.data.mybatis.service.EnhancedEntityService;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.commons.domain.request.BizTypeRequest;
import cc.wdev.platform.system.commons.domain.request.RelationRequest;
import cc.wdev.platform.system.commons.domain.request.SequenceRequest;
import cc.wdev.platform.system.commons.domain.vo.RelationVo;
import cc.wdev.platform.system.dict.domain.entity.DictEntity;
import cc.wdev.platform.system.dict.domain.request.*;
import cc.wdev.platform.system.dict.domain.vo.DictTypeVo;
import cc.wdev.platform.system.dict.domain.vo.DictVo;
import cc.wdev.platform.system.dict.repository.DictRepository;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
public interface DictService
    extends CachingEntityService<DictEntity, Long>, EnhancedEntityService<DictEntity, Long, DictRepository> {

    Boolean checkCode(DictCodeCheckRequest request);

    /**
     * 保存字典项
     */
    void saveDict(DictSaveRequest request);

    /**
     * 根据id获取字典项
     */
    DictEntity findById(Long id);

    /**
     * 根据id获取字典项
     */
    DictVo getDict(DictRequest request);

    /**
     * 根据code获取字典项
     */
    DictEntity findByCode(DictRequest request);

    /**
     * 根据业务类型和编号获取字典项
     */
    DictEntity findByBizTypeAndCode(String bizType, String code);

    /**
     * 根据id字典项
     */
    void deleteById(Long id);

    /**
     * 根据code删除字典项
     */
    void deleteByCode(DictRequest request);

    /**
     * 获取字典类型
     */
    DictTypeVo getDictType(BizTypeRequest request);

    /**
     * 获取字典类型关联
     */
    RelationVo<DictVo> getRelation(RelationRequest request);

    /**
     * 搜索指定标签类型下面的标签
     */
    Page<DictVo> search(DictSearchRequest request);

    /**
     * 搜索指定标签类型下面的标签
     */
    List<DictVo> list(DictSearchRequest request);

    /**
     * 搜索指定标签类型下面的标签
     */
    Page<DictEntity> findByPage(DictSearchRequest request);


    /**
     * 根据类型删除字典所有字典项
     */
    void deleteByType(String bizType);

    /**
     * 检查关联关系是否存在
     */
    boolean checkRelationExists(RelationRequest request);

    /**
     * 统计关联关系数量
     */
    long countRelations(RelationRequest request);

    /**
     * 重置序列（恢复默认顺序）
     */
    void resetSequence(SequenceRequest request);

    /**
     * 根据个性化排序查询字典列表
     */
    List<DictVo> findDictsWithSequence(SequenceRequest request);

    /**
     * 获取字典类型关联
     */
    Map<Long, RelationVo<DictVo>> relationMap(RelationRequest request);

    /**
     * 删除字典
     */
    void deleteDict(DictDeleteRequest request);

    /**
     * 检查标题是否可用
     */
    Boolean checkTitle(DictTitleCheckRequest request);
}
