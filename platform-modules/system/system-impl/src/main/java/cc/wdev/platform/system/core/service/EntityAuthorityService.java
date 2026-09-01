package cc.wdev.platform.system.core.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.commons.enums.EntityAuthorityBizTypeEnum;
import cc.wdev.platform.system.core.domain.entity.EntityAuthorityEntity;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface EntityAuthorityService extends CachingEntityService<EntityAuthorityEntity, Long> {

    /**
     * 获取实体权限数组
     *
     * @param bizType 业务类型枚举
     * @param bizId   业务ID
     * @return {@link List }<{@link EntityAuthorityEntity }>
     */
    Set<Long> findAuthorityIds(String bizType, Long bizId);

    /**
     * 获取实体权限数组
     *
     * @param bizType 业务类型枚举
     * @param bizId   业务ID
     * @return {@link List }<{@link EntityAuthorityEntity }>
     */
    Set<Long> findAuthorityIds(EntityAuthorityBizTypeEnum bizType, Long bizId);

    /**
     * 获取实体权限数组
     *
     * @param bizType 业务类型枚举
     * @param bizIds  业务ID数组
     * @return {@link List }<{@link EntityAuthorityEntity }>
     */
    Set<Long> findAuthorityIds(EntityAuthorityBizTypeEnum bizType, Collection<Long> bizIds);

    /**
     * 删除实体权限
     *
     * @param bizTypeEnum 实体类型
     * @param bizId       实体ID
     */
    void deleteAuthority(EntityAuthorityBizTypeEnum bizTypeEnum, Long bizId);

    /**
     * 删除实体权限
     *
     * @param bizTypeEnum 实体类型
     * @param bizIds      实体ID数组
     */
    void deleteAuthority(EntityAuthorityBizTypeEnum bizTypeEnum, List<Long> bizIds);

    /**
     * 保存实体权限
     *
     * @param bizType      业务类型
     * @param bizId        业务ID
     * @param authorityIds 权限ID数组
     * @return boolean 权限是否发生改变
     */
    boolean saveAuthority(EntityAuthorityBizTypeEnum bizType, Long bizId, Collection<Long> authorityIds);

}
