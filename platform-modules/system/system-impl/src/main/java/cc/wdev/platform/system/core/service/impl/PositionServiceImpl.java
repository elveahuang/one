package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleTenantCacheKeyGenerator;
import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.constants.SystemCacheConstants;
import cc.wdev.platform.system.core.domain.converter.PositionConverter;
import cc.wdev.platform.system.core.domain.dto.PositionDto;
import cc.wdev.platform.system.core.domain.dto.PositionSaveDto;
import cc.wdev.platform.system.core.domain.entity.PositionEntity;
import cc.wdev.platform.system.core.domain.request.PositionSearchRequest;
import cc.wdev.platform.system.core.domain.vo.PositionVo;
import cc.wdev.platform.system.core.repository.PositionRepository;
import cc.wdev.platform.system.core.service.PositionService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;

/**
 * @author elvea
 * @see PositionService
 * @see BaseCachingEntityService
 */
@Service
public class PositionServiceImpl extends BaseCachingEntityService<PositionEntity, Long, PositionRepository>
    implements PositionService {

    private final CacheKeyGenerator cacheKeyGenerator = SimpleTenantCacheKeyGenerator.builder().prefix(SystemCacheConstants.POSITION).build();

    /**
     * @see BaseCachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see PositionService#findPositionTree()
     */
    @Override
    public List<PositionVo> findPositionTree() {
        List<PositionEntity> entityList = lambdaQueryWrapper()
            .eq(PositionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByAsc(PositionEntity::getId)
            .list();
        return buildTree(PositionConverter.INSTANCE.entityList2VoList(entityList));
    }

    /**
     * @see PositionService#findPositionPage(PositionSearchRequest)
     */
    @Override
    public Page<PositionVo> findPositionPage(PositionSearchRequest request) {
        IPage<PositionEntity> page = lambdaQueryWrapper()
            .and(StringUtils.isNotEmpty(request.getQ()), wrapper -> wrapper
                .like(PositionEntity::getCode, request.getQ())
                .or().like(PositionEntity::getTitle, request.getQ())
                .or().like(PositionEntity::getLabel, request.getQ()))
            .eq(ObjectUtils.isValidId(request.getParentId()), PositionEntity::getParentId, request.getParentId())
            .eq(PositionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByAsc(PositionEntity::getId)
            .page(getMyBatisPlusPage(request.getPageable()));

        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            List<PositionVo> list = page.getRecords().stream().map(PositionConverter.INSTANCE::entity2Vo).toList();
            return MyBatisPlusUtils.toSpringDataPage(page, list);
        }
        return SpringDataUtils.emptyPage();
    }

    /**
     * @see PositionService#findPositionDetails(Long)
     */
    @Override
    public PositionVo findPositionDetails(Long id) {
        PositionEntity entity = this.checkExistsOrFail(id, ResponseCodeEnum.POSITION__NOT_PRESENT);
        return PositionConverter.INSTANCE.entity2Vo(entity);
    }

    /**
     * @see PositionService#savePosition(PositionSaveDto)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PositionDto savePosition(PositionSaveDto saveDto) {
        PositionEntity entity;
        if (ObjectUtils.isValidId(saveDto.getId())) {
            entity = this.checkExistsOrFail(saveDto.getId(), ResponseCodeEnum.POSITION__NOT_PRESENT);
            String originalCode = entity.getCode();
            Integer originalStatus = entity.getStatus();
            PositionConverter.INSTANCE.updateEntityFromSaveDto(saveDto, entity);
            // 未指定编号和状态则保留原值
            if (StringUtils.isEmpty(entity.getCode())) {
                entity.setCode(originalCode);
            }
            if (entity.getStatus() == null) {
                entity.setStatus(originalStatus);
            }
        } else {
            saveDto.setId(null);
            entity = PositionConverter.INSTANCE.saveDto2Entity(saveDto);
        }

        // 顶层岗位不允许编辑父级岗位
        if (BooleanTypeEnum.isTrueValue(entity.getRootInd())) {
            entity.setParentId(0L);
        } else {
            // 父级岗位未指定则默认挂在顶层岗位下
            if (!ObjectUtils.isValidId(entity.getParentId())) {
                PositionEntity rootPosition = this.getRootPosition();
                if (rootPosition != null) {
                    entity.setParentId(rootPosition.getId());
                }
            }
            // 校验父级岗位
            PositionEntity parent = ObjectUtils.isValidId(entity.getParentId()) ?
                this.checkExistsOrFail(entity.getParentId(), ResponseCodeEnum.POSITION__NOT_PRESENT) : null;
            // 不允许把自己设置为自己的父级岗位，也不允许把父级岗位设置为自己的下级岗位
            if (parent != null && (Objects.equals(parent.getId(), entity.getId()) || this.isDescendant(entity.getId(), parent.getId()))) {
                throw new ServiceException(ResponseCodeEnum.PARAM_ERROR);
            }
        }

        // 检查编号是否可用
        if (StringUtils.isNotEmpty(entity.getCode()) && this.existsByCode(entity.getCode(), entity.getId())) {
            throw new ServiceException(ResponseCodeEnum.POSITION__CODE_NOT_AVAILABLE);
        }
        // 编号为空则自动生成唯一编号
        if (StringUtils.isEmpty(entity.getCode())) {
            entity.setCode(generateCode("POS"));
        }
        // 状态为空则默认启用
        if (entity.getStatus() == null) {
            entity.setStatus(StatusTypeEnum.ON.getValue());
        }

        this.save(entity);
        return PositionConverter.INSTANCE.entity2Dto(entity);
    }

    /**
     * @see PositionService#deletePosition(List)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePosition(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        List<PositionEntity> entityList = this.findByIds(Sets.newHashSet(ids));
        if (CollectionUtils.isEmpty(entityList)) {
            return;
        }
        // 顶层岗位不允许删除
        if (entityList.stream().anyMatch((e) -> BooleanTypeEnum.isTrueValue(e.getRootInd()))) {
            throw new ServiceException(ResponseCodeEnum.POSITION__ROOT_NOT_ALLOWED);
        }
        // 存在下级岗位则不允许删除
        long childrenCount = lambdaQueryWrapper()
            .in(PositionEntity::getParentId, ids)
            .eq(PositionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .count();
        if (childrenCount > 0) {
            throw new ServiceException(ResponseCodeEnum.POSITION__EXISTS_CHILDREN);
        }
        this.softDeleteBatchById(ids);
    }

    /**
     * @see PositionService#existsByCode(String, Long)
     */
    @Override
    public boolean existsByCode(String code, Long id) {
        if (StringUtils.isEmpty(code)) {
            return false;
        }
        return lambdaQueryWrapper()
            .eq(PositionEntity::getCode, code)
            .eq(PositionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .ne(ObjectUtils.isValidId(id), PositionEntity::getId, id)
            .exists();
    }

    /**
     * @see PositionService#getRootPosition()
     */
    @Override
    public PositionEntity getRootPosition() {
        return findOne(lambdaQueryWrapper()
            .eq(PositionEntity::getRootInd, BooleanTypeEnum.TRUE.getValue())
            .eq(PositionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
        );
    }

    /**
     * @see PositionService#getDefaultPosition()
     */
    @Override
    public PositionEntity getDefaultPosition() {
        return findOne(lambdaQueryWrapper()
            .eq(PositionEntity::getDefaultInd, BooleanTypeEnum.TRUE.getValue())
            .eq(PositionEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
        );
    }

    /**
     * 判断 entityId 是否为 ancestorId 的下级岗位
     */
    private boolean isDescendant(Long entityId, Long ancestorId) {
        if (!ObjectUtils.isValidId(entityId) || !ObjectUtils.isValidId(ancestorId)) {
            return false;
        }
        Long parentId = ancestorId;
        while (ObjectUtils.isValidId(parentId) && parentId > 0) {
            if (Objects.equals(parentId, entityId)) {
                return true;
            }
            PositionEntity parent = this.findCacheById(parentId);
            if (parent == null) {
                return false;
            }
            parentId = parent.getParentId();
        }
        return false;
    }

    /**
     * 组装岗位树
     */
    private static List<PositionVo> buildTree(List<PositionVo> voList) {
        if (CollectionUtils.isEmpty(voList)) {
            return Lists.newArrayList();
        }
        Map<Long, PositionVo> voMap = Maps.newLinkedHashMapWithExpectedSize(voList.size());
        voList.forEach((vo) -> voMap.put(vo.getId(), vo));

        List<PositionVo> tree = Lists.newArrayList();
        for (PositionVo vo : voList) {
            vo.setChildren(Lists.newArrayList());
            PositionVo parent = voMap.get(vo.getParentId());
            if (parent == null || Objects.equals(parent.getId(), vo.getId())) {
                tree.add(vo);
            } else {
                parent.getChildren().add(vo);
            }
        }
        return tree;
    }

}
