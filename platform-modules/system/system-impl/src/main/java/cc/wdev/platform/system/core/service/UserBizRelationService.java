package cc.wdev.platform.system.core.service;

import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.core.domain.entity.UserBizRelationEntity;
import cc.wdev.platform.system.core.domain.request.UserBizRelationReq;
import cc.wdev.platform.system.core.domain.request.UserBizRelationSearchReq;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

public interface UserBizRelationService extends EntityService<UserBizRelationEntity, Long> {

    void saveRelation(@Valid UserBizRelationReq saveReq);

    void deleteRelation(UserBizRelationReq req);

    UserBizRelationEntity getRelation(UserBizRelationReq req);

    Map<Long, Integer> batchHasRelation(@Valid UserBizRelationReq req);

    Map<Long, LocalDateTime> batchDateTime(@Valid UserBizRelationReq req);

    Collection<Long> getBizIds(@Valid UserBizRelationSearchReq req);
}
