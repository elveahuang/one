package cc.wdev.webapp.mybatis.service;

import cc.wdev.platform.commons.data.mybatis.service.EnhancedEntityService;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.webapp.mybatis.domain.entity.MpUserEntity;
import cc.wdev.webapp.mybatis.repository.MpUserRepository;

public interface MpUserService
    extends EntityService<MpUserEntity, Long>, EnhancedEntityService<MpUserEntity, Long, MpUserRepository> {
}
