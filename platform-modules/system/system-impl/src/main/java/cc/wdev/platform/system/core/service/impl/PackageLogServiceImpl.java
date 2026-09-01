package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.system.core.domain.entity.PackageLogEntity;
import cc.wdev.platform.system.core.repository.PackageLogRepository;
import cc.wdev.platform.system.core.service.PackageLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author erden
 */
@Slf4j
@Service
public class PackageLogServiceImpl extends BaseEntityService<PackageLogEntity, Long, PackageLogRepository> implements PackageLogService {
}
