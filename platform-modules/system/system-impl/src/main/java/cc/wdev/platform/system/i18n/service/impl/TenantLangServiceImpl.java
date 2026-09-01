package cc.wdev.platform.system.i18n.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.system.i18n.domain.entity.TenantLangEntity;
import cc.wdev.platform.system.i18n.repository.TenantLangRepository;
import cc.wdev.platform.system.i18n.service.TenantLangService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author erden
 */
@Slf4j
@Service
public class TenantLangServiceImpl extends BaseCachingEntityService<TenantLangEntity, Long, TenantLangRepository> implements TenantLangService {

}
