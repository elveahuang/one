package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.system.core.domain.entity.PackageItemEntity;
import cc.wdev.platform.system.core.repository.PackageItemRepository;
import cc.wdev.platform.system.core.service.PackageItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author erden
 */
@Slf4j
@Service
public class PackageItemServiceImpl extends BaseCachingEntityService<PackageItemEntity, Long, PackageItemRepository> implements PackageItemService {
}
