package cc.wdev.platform.system.open.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.open.domain.entity.WxMpAppEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 微信公众号应用 Repository
 */
@Mapper
public interface WxMpAppRepository extends BaseEntityRepository<WxMpAppEntity, Long> {
}
