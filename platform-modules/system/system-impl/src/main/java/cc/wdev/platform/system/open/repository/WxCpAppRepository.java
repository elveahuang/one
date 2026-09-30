package cc.wdev.platform.system.open.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.open.domain.entity.WxCpAppEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业微信应用 Repository
 */
@Mapper
public interface WxCpAppRepository extends BaseEntityRepository<WxCpAppEntity, Long> {
}
