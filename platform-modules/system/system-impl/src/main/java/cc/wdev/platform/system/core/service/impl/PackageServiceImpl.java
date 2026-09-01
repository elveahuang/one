package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.system.core.domain.entity.PackageEntity;
import cc.wdev.platform.system.core.repository.PackageRepository;
import cc.wdev.platform.system.core.service.PackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author erden
 */
@Slf4j
@Service
public class PackageServiceImpl extends BaseCachingEntityService<PackageEntity, Long, PackageRepository> implements PackageService {
}
