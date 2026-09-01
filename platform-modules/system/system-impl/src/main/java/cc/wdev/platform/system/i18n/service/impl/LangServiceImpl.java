package cc.wdev.platform.system.i18n.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.system.i18n.domain.entity.LangEntity;
import cc.wdev.platform.system.i18n.repository.LangRepository;
import cc.wdev.platform.system.i18n.service.LangService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author erden
 */
@Slf4j
@Service
public class LangServiceImpl extends BaseCachingEntityService<LangEntity, Long, LangRepository> implements LangService {

}
