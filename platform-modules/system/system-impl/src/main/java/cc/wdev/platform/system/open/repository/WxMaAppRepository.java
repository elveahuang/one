package cc.wdev.platform.system.open.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.open.domain.entity.WxMaAppEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 微信小程序应用 Repository
 */
@Mapper
public interface WxMaAppRepository extends BaseEntityRepository<WxMaAppEntity, Long> {
}
