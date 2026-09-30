package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.enums.SourceTypeEnum;
import cc.wdev.platform.commons.security.domain.SocialUser;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.core.cache.EntityOpenIdEntityCacheKeyGenerator;
import cc.wdev.platform.system.core.domain.entity.EntityOpenIdEntity;
import cc.wdev.platform.system.core.repository.EntityOpenIdRepository;
import cc.wdev.platform.system.core.service.EntityOpenIdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author erden
 */
@Slf4j
@Service
public class EntityOpenIdServiceImpl extends BaseCachingEntityService<EntityOpenIdEntity, Long, EntityOpenIdRepository> implements EntityOpenIdService {

    private final EntityOpenIdEntityCacheKeyGenerator cacheKeyGenerator = new EntityOpenIdEntityCacheKeyGenerator();

    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    @Override
    public EntityOpenIdEntity findEntityByOpenId(String bizType, String openId) {
        if (StringUtils.isBlank(bizType) || StringUtils.isBlank(openId)) {
            return null;
        }
        return this.getCacheService().get(this.cacheKeyGenerator.keyByOpenId(bizType, openId), k -> {
            return this.findOneByWrapper(this.lambdaQueryWrapper()
                .eq(EntityOpenIdEntity::getBizType, bizType)
                .eq(EntityOpenIdEntity::getOpenId, openId));
        });
    }

    @Override
    public EntityOpenIdEntity findEntityByBizId(String bizType, Long bizId) {
        if (!ObjectUtils.isValidId(bizId) || StringUtils.isBlank(bizType)) {
            return null;
        }
        return this.getCacheService().get(this.cacheKeyGenerator.keyByBizId(bizType, bizId), k -> {
            return this.findOneByWrapper(this.lambdaQueryWrapper()
                .eq(EntityOpenIdEntity::getBizType, bizType)
                .eq(EntityOpenIdEntity::getBizId, bizId));
        });
    }

    @Override
    public void saveOpenId(Long bizId, String bizType, SocialUser socialUser) {
        String openId = socialUser.getOpenId();
        EntityOpenIdEntity entity = this.findEntityByOpenId(bizType, openId);
        if (entity != null) {
            EntityOpenIdEntity update = new EntityOpenIdEntity();
            update.setId(entity.getId());
            update.setBizId(bizId);
            update.setNickname(socialUser.getNickname());
            update.setAvatar(socialUser.getHeadImgUrl());
            this.updateById(update);
            return;
        }

        entity = EntityOpenIdEntity.builder()
            .openId(openId)
            .unionId(socialUser.getUnionId())
            .nickname(socialUser.getNickname())
            .avatar(socialUser.getHeadImgUrl())
            .bizId(bizId)
            .bizType(bizType)
            .source(SourceTypeEnum.SYNC.getValue())
            .build();
        this.save(entity);
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void setCache(EntityOpenIdEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().set(this.cacheKeyGenerator.byId(model.getId()), model);
            }
            if (StringUtils.isNotBlank(model.getBizType()) && StringUtils.isNotBlank(model.getOpenId())) {
                getCacheService().set(this.cacheKeyGenerator.keyByOpenId(model.getBizType(), model.getOpenId()), model);
            }
            if (StringUtils.isNotBlank(model.getBizType()) && ObjectUtils.isValidId(model.getBizId())) {
                getCacheService().set(this.cacheKeyGenerator.keyByBizId(model.getBizType(), model.getBizId()), model);
            }
        }
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void deleteCache(EntityOpenIdEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().delete(this.cacheKeyGenerator.byId(model.getId()));
            }
            if (StringUtils.isNotBlank(model.getBizType()) && StringUtils.isNotBlank(model.getOpenId())) {
                getCacheService().delete(this.cacheKeyGenerator.keyByOpenId(model.getBizType(), model.getOpenId()));
            }
            if (StringUtils.isNotBlank(model.getBizType()) && ObjectUtils.isValidId(model.getBizId())) {
                getCacheService().delete(this.cacheKeyGenerator.keyByBizId(model.getBizType(), model.getBizId()));
            }
        }
    }
}
