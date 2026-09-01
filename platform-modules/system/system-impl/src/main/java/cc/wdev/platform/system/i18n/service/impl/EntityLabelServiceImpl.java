package cc.wdev.platform.system.i18n.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.system.i18n.domain.entity.EntityLabelEntity;
import cc.wdev.platform.system.i18n.repository.EntityLabelRepository;
import cc.wdev.platform.system.i18n.service.EntityLabelService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author elvea
 * @see EntityLabelService
 * @see BaseCachingEntityService
 */
@Slf4j
@AllArgsConstructor
@Service
public class EntityLabelServiceImpl extends BaseCachingEntityService<EntityLabelEntity, Long, EntityLabelRepository> implements EntityLabelService {
}
