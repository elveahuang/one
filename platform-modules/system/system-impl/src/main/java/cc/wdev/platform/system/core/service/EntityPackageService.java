package cc.wdev.platform.system.core.service;

import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.commons.enums.EntityPackageBizTypeEnum;
import cc.wdev.platform.system.core.domain.entity.EntityPackageEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author erden
 */
public interface EntityPackageService extends EntityService<EntityPackageEntity, Long> {

    /**
     * 获取实体套餐ID数组
     *
     * @param bizTypeEnum 业务类型
     * @param entityId    业务ID
     * @return {@link Set }<{@link Long }>
     */
    Set<Long> findPackageIds(EntityPackageBizTypeEnum bizTypeEnum, Long entityId);

    /**
     * 删除实体套餐
     *
     * @param bizTypeEnum 业务类型
     * @param entityId    业务实体ID
     */
    void deletePackage(EntityPackageBizTypeEnum bizTypeEnum, Long entityId);

    /**
     * 根据套餐ID删除套餐关联
     *
     * @param packageId 套餐ID
     */
    void deletePackageByPackageId(Long packageId);


    /**
     * 根据套餐ID数组删除套餐关联
     *
     * @param packageIds 套餐ID数组
     */
    void deletePackageByPackageIds(List<Long> packageIds);

    /**
     * 保存实体套餐
     *
     * @param bizTypeEnum 业务类型
     * @param entityId    业务ID
     * @param packageIds  套餐ID数组
     * @return boolean 权限是否发生改变
     */
    boolean savePackage(EntityPackageBizTypeEnum bizTypeEnum, Long entityId, List<Long> packageIds);


    /**
     * 批量获取关联套餐
     *
     * @param entityIds 实体ID数组
     * @return {@link Map }<{@link Long }, {@link List }<{@link Long }>>
     */
    Map<Long, List<Long>> packageIdsMap(List<Long> entityIds, EntityPackageBizTypeEnum bizTypeEnum);
}
