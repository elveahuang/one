package cc.wdev.webapp.mybatis.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.webapp.mybatis.domain.entity.MpUserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MpUserRepository extends BaseEntityRepository<MpUserEntity, Long> {
}
