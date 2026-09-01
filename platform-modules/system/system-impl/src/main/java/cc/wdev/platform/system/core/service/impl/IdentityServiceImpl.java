package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.system.core.domain.entity.IdentityEntity;
import cc.wdev.platform.system.core.repository.IdentityRepository;
import cc.wdev.platform.system.core.service.IdentityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author erden
 */
@Slf4j
@Service
public class IdentityServiceImpl extends BaseCachingEntityService<IdentityEntity, Long, IdentityRepository> implements IdentityService {

}
