package cc.wdev.platform.commons.data.mybatis.repository;

import cc.wdev.platform.commons.data.core.domain.IdEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * @param <T>
 * @param <K>
 * @author elvea
 * @see BaseMapper
 */
@NoRepositoryBean
public interface BaseEntityRepository<T extends IdEntity, K extends Serializable> extends BaseMapper<T> {
}
