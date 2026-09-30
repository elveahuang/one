package cc.wdev.platform.system.log.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.log.domain.entity.SearchLogEntity;
import cc.wdev.platform.system.log.domain.request.SearchLogSearchRequest;
import cc.wdev.platform.system.log.domain.vo.SearchLogVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author elvea
 */
@Mapper
public interface SearchLogRepository extends BaseEntityRepository<SearchLogEntity, Long> {

    List<SearchLogVo> getSearchLogList(@Param("page") Page<?> page,
                                       @Param("params") SearchLogSearchRequest request);

    List<SearchLogVo> getSearchLogHotList(@Param("page") Page<?> page,
                                          @Param("params") SearchLogSearchRequest request);

}
