package cc.wdev.platform.system.core.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.commons.domain.chart.DayAndCount;
import cc.wdev.platform.system.commons.domain.chart.PieData;
import cc.wdev.platform.system.core.domain.entity.LoginSessionEntity;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author elvea
 */
@Mapper
public interface LoginSessionRepository extends BaseEntityRepository<LoginSessionEntity, Long> {
    /**
     * 获取对应终端登录数
     */
    List<DayAndCount> getCountByPeriod(@Param("type") Integer type, @Param("year") Integer year, @Param("month") Integer month, @Param("day") Integer day, @Param("goHeavy") boolean goHeavy);

    List<PieData> getCountByPlatform(@Param("year") Integer year, @Param("month") Integer month, @Param("day") Integer day);

    /**
     * 获取所有登录数
     */
    @InterceptorIgnore(tenantLine = "true")
    long getAllLoginCount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
