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
import cc.wdev.platform.system.commons.enums.EntityRelationBizTypeEnum;
import cc.wdev.platform.system.core.domain.converter.OrganizationConverter;
import cc.wdev.platform.system.core.domain.dto.OrganizationDto;
import cc.wdev.platform.system.core.domain.dto.OrganizationSaveDto;
import cc.wdev.platform.system.core.domain.entity.EntityRelationEntity;
import cc.wdev.platform.system.core.domain.entity.OrganizationEntity;
import cc.wdev.platform.system.core.domain.request.OrganizationSearchRequest;
import cc.wdev.platform.system.core.domain.vo.OrganizationVo;
import cc.wdev.platform.system.core.repository.OrganizationRepository;
import cc.wdev.platform.system.core.service.EntityRelationService;
import cc.wdev.platform.system.core.service.OrganizationService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;

/**
 * @author elvea
 * @see OrganizationService
 * @see BaseCachingEntityService
 */
@Service
@AllArgsConstructor
public class OrganizationServiceImpl extends BaseCachingEntityService<OrganizationEntity, Long, OrganizationRepository>
    implements OrganizationService {

    private final CacheKeyGenerator cacheKeyGenerator = SimpleTenantCacheKeyGenerator.builder().prefix(SystemCacheConstants.ORGANIZATION).build();

    private final EntityRelationService entityRelationService;

    /**
     * @see BaseCachingEntityService#getCacheKeyGenerator()
     */
    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see OrganizationService#findOrganizationTree()
     */
    @Override
    public List<OrganizationVo> findOrganizationTree() {
        List<OrganizationEntity> entityList = lambdaQueryWrapper()
            .eq(OrganizationEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByAsc(OrganizationEntity::getId)
            .list();
        return buildTree(OrganizationConverter.INSTANCE.entityList2VoList(entityList));
    }

    /**
     * @see OrganizationService#findOrganizationPage(OrganizationSearchRequest)
     */
    @Override
    public Page<OrganizationVo> findOrganizationPage(OrganizationSearchRequest request) {
        LambdaQueryChainWrapper<OrganizationEntity> wrapper = lambdaQueryWrapper()
            .and(StringUtils.isNotEmpty(request.getQ()), w -> w
                .like(OrganizationEntity::getCode, request.getQ())
                .or().like(OrganizationEntity::getTitle, request.getQ())
                .or().like(OrganizationEntity::getLabel, request.getQ()))
            .eq(ObjectUtils.isValidId(request.getParentId()), OrganizationEntity::getParentId, request.getParentId())
            .eq(request.getStatus() != null, OrganizationEntity::getStatus, request.getStatus())
            .eq(OrganizationEntity::getActive, ActiveTypeEnum.ENABLED.getValue());
        applySort(request.getSort(), request.getOrder(), wrapper);
        IPage<OrganizationEntity> page = wrapper.page(getMyBatisPlusPage(request.getPageable()));

        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            List<OrganizationVo> list = page.getRecords().stream().map(OrganizationConverter.INSTANCE::entity2Vo).toList();
            return MyBatisPlusUtils.toSpringDataPage(page, list);
        }
        return SpringDataUtils.emptyPage();
    }

    /**
     * @see OrganizationService#findOrganizationOptions()
     */
    @Override
    public List<OrganizationVo> findOrganizationOptions() {
        return lambdaQueryWrapper()
            .ne(OrganizationEntity::getRootInd, BooleanTypeEnum.TRUE.getValue())
            .eq(OrganizationEntity::getStatus, StatusTypeEnum.ON.getValue())
            .eq(OrganizationEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByAsc(OrganizationEntity::getId)
            .list()
            .stream()
            .map(OrganizationConverter.INSTANCE::entity2Vo)
            .toList();
    }

    /**
     * @see OrganizationService#findOrganizationDetails(Long)
     */
    @Override
    public OrganizationVo findOrganizationDetails(Long id) {
        OrganizationEntity entity = this.checkExistsOrFail(id, ResponseCodeEnum.ORGANIZATION__NOT_PRESENT);
        return OrganizationConverter.INSTANCE.entity2Vo(entity);
    }

    /**
     * @see OrganizationService#saveOrganization(OrganizationSaveDto)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrganizationDto saveOrganization(OrganizationSaveDto saveDto) {
        OrganizationEntity entity;
        if (ObjectUtils.isValidId(saveDto.getId())) {
            entity = this.checkExistsOrFail(saveDto.getId(), ResponseCodeEnum.ORGANIZATION__NOT_PRESENT);
            String originalCode = entity.getCode();
            Integer originalStatus = entity.getStatus();
            OrganizationConverter.INSTANCE.updateEntityFromSaveDto(saveDto, entity);
            // 未指定编号和状态则保留原值
            if (StringUtils.isEmpty(entity.getCode())) {
                entity.setCode(originalCode);
            }
            if (entity.getStatus() == null) {
                entity.setStatus(originalStatus);
            }
        } else {
            saveDto.setId(null);
            entity = OrganizationConverter.INSTANCE.saveDto2Entity(saveDto);
        }

        // 顶层部门不允许编辑父级部门
        if (BooleanTypeEnum.isTrueValue(entity.getRootInd())) {
            entity.setParentId(0L);
        } else {
            // 父级部门未指定则默认挂在顶层部门下
            if (!ObjectUtils.isValidId(entity.getParentId())) {
                OrganizationEntity rootOrganization = this.getRootOrganization();
                if (rootOrganization != null) {
                    entity.setParentId(rootOrganization.getId());
                }
            }
            // 校验父级部门
            OrganizationEntity parent = ObjectUtils.isValidId(entity.getParentId()) ?
                this.checkExistsOrFail(entity.getParentId(), ResponseCodeEnum.ORGANIZATION__NOT_PRESENT) : null;
            if (parent != null) {
                // 不允许选择已停用的父级部门
                if (!Objects.equals(parent.getStatus(), StatusTypeEnum.ON.getValue())) {
                    throw new ServiceException(ResponseCodeEnum.ORGANIZATION__INVALID_PARENT);
                }
                // 不允许把自己设置为自己的父级部门，也不允许把父级部门设置为自己的下级部门
                if (Objects.equals(parent.getId(), entity.getId()) || this.isDescendant(entity.getId(), parent.getId())) {
                    throw new ServiceException(ResponseCodeEnum.ORGANIZATION__INVALID_PARENT);
                }
            }
        }

        // 检查编号是否可用
        if (StringUtils.isNotEmpty(entity.getCode()) && this.existsByCode(entity.getCode(), entity.getId())) {
            throw new ServiceException(ResponseCodeEnum.ORGANIZATION__CODE_NOT_AVAILABLE);
        }
        // 编号为空则自动生成唯一编号
        if (StringUtils.isEmpty(entity.getCode())) {
            entity.setCode(generateCode("ORG"));
        }
        // 状态为空则默认启用
        if (entity.getStatus() == null) {
            entity.setStatus(StatusTypeEnum.ON.getValue());
        }

        this.save(entity);
        return OrganizationConverter.INSTANCE.entity2Dto(entity);
    }

    /**
     * @see OrganizationService#deleteOrganization(List)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrganization(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        List<OrganizationEntity> entityList = this.findByIds(Sets.newHashSet(ids));
        if (CollectionUtils.isEmpty(entityList)) {
            return;
        }
        // 顶层部门不允许删除
        if (entityList.stream().anyMatch((e) -> BooleanTypeEnum.isTrueValue(e.getRootInd()))) {
            throw new ServiceException(ResponseCodeEnum.ORGANIZATION__ROOT_NOT_ALLOWED);
        }
        // 存在下级部门则不允许删除
        long childrenCount = lambdaQueryWrapper()
            .in(OrganizationEntity::getParentId, ids)
            .eq(OrganizationEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .count();
        if (childrenCount > 0) {
            throw new ServiceException(ResponseCodeEnum.ORGANIZATION__EXISTS_CHILDREN);
        }
        // 存在关联用户则不允许删除
        List<EntityRelationEntity> userRelations = entityRelationService.getChildren(EntityRelationBizTypeEnum.USR_CURRENT_ORG.getValue(), ids);
        if (CollectionUtils.isNotEmpty(userRelations)) {
            throw new ServiceException(ResponseCodeEnum.ORGANIZATION__EXISTS_USERS);
        }
        this.softDeleteBatchById(ids);
    }

    /**
     * @see OrganizationService#existsByCode(String, Long)
     */
    @Override
    public boolean existsByCode(String code, Long id) {
        if (StringUtils.isEmpty(code)) {
            return false;
        }
        return lambdaQueryWrapper()
            .eq(OrganizationEntity::getCode, code)
            .eq(OrganizationEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .ne(ObjectUtils.isValidId(id), OrganizationEntity::getId, id)
            .exists();
    }

    /**
     * @see OrganizationService#getRootOrganization()
     */
    @Override
    public OrganizationEntity getRootOrganization() {
        return findOne(lambdaQueryWrapper()
            .eq(OrganizationEntity::getRootInd, BooleanTypeEnum.TRUE.getValue())
            .eq(OrganizationEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
        );
    }

    /**
     * @see OrganizationService#getDefaultOrganization()
     */
    @Override
    public OrganizationEntity getDefaultOrganization() {
        return findOne(lambdaQueryWrapper()
            .eq(OrganizationEntity::getDefaultInd, BooleanTypeEnum.TRUE.getValue())
            .eq(OrganizationEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
        );
    }

    /**
     * 判断 entityId 是否为 ancestorId 的下级部门
     */
    private boolean isDescendant(Long entityId, Long ancestorId) {
        if (!ObjectUtils.isValidId(entityId) || !ObjectUtils.isValidId(ancestorId)) {
            return false;
        }
        Set<Long> visited = Sets.newHashSet();
        Long parentId = ancestorId;
        while (ObjectUtils.isValidId(parentId) && parentId > 0 && visited.add(parentId)) {
            if (Objects.equals(parentId, entityId)) {
                return true;
            }
            OrganizationEntity parent = this.findCacheById(parentId);
            if (parent == null) {
                return false;
            }
            parentId = parent.getParentId();
        }
        return false;
    }

    /**
     * 应用排序条件，排序字段仅允许白名单内的列
     */
    private static void applySort(String sort, String order, LambdaQueryChainWrapper<OrganizationEntity> wrapper) {
        boolean asc = !"desc".equalsIgnoreCase(order);
        switch (StringUtils.isNotEmpty(sort) ? sort : "id") {
            case "code" -> wrapper.orderBy(true, asc, OrganizationEntity::getCode);
            case "title" -> wrapper.orderBy(true, asc, OrganizationEntity::getTitle);
            case "created_at", "createdAt" -> wrapper.orderBy(true, asc, OrganizationEntity::getCreatedAt);
            case "updated_at", "updatedAt" -> wrapper.orderBy(true, asc, OrganizationEntity::getUpdatedAt);
            default -> wrapper.orderByAsc(OrganizationEntity::getId);
        }
    }

    /**
     * 组装部门树
     */
    static List<OrganizationVo> buildTree(List<OrganizationVo> voList) {
        if (CollectionUtils.isEmpty(voList)) {
            return Lists.newArrayList();
        }
        Map<Long, OrganizationVo> voMap = Maps.newLinkedHashMapWithExpectedSize(voList.size());
        voList.forEach((vo) -> voMap.put(vo.getId(), vo));

        List<OrganizationVo> tree = Lists.newArrayList();
        for (OrganizationVo vo : voList) {
            vo.setChildren(Lists.newArrayList());
            OrganizationVo parent = voMap.get(vo.getParentId());
            if (parent == null || Objects.equals(parent.getId(), vo.getId())) {
                tree.add(vo);
            } else {
                parent.getChildren().add(vo);
            }
        }
        return tree;
    }

}
