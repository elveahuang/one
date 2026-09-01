package cc.wdev.webapp.mybatis.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.webapp.mybatis.domain.entity.MpUserEntity;
import cc.wdev.webapp.mybatis.repository.MpUserRepository;
import cc.wdev.webapp.mybatis.service.MpUserCachingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MpUserCachingServiceImpl extends BaseCachingEntityService<MpUserEntity, Long, MpUserRepository> implements MpUserCachingService {
}
