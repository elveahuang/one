package cc.wdev.platform.system.core.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.core.domain.entity.AuthorityEntity;
import cc.wdev.platform.system.core.domain.vo.AuthorityVo;

import java.util.Collection;
import java.util.List;

/**
 * @author elvea
 */
public interface AuthorityService extends CachingEntityService<AuthorityEntity, Long> {

    /**
     * 获取指定用户所有的权限
     *
     * @param userId 用户ID
     * @return 权限
     */
    List<AuthorityEntity> findByUserId(Long userId);

    /**
     * 获取权限数组
     *
     * @return {@link List }<{@link AuthorityVo }>
     */
    List<AuthorityVo> findAuthorityVoList();

    /**
     * 获取实体权限ID数组
     */
    Collection<Long> findAuthorityIds(String bizType, Long bizId);
}
