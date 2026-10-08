package cc.wdev.platform.system.i18n.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.i18n.domain.entity.TenantLangEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author erden
 */
@Mapper
public interface TenantLangRepository extends BaseEntityRepository<TenantLangEntity, Long> {
}
