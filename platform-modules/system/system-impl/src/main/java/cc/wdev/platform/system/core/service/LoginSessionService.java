package cc.wdev.platform.system.core.service;

import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.system.commons.domain.chart.ChartVo;
import cc.wdev.platform.system.commons.domain.chart.DayAndCount;
import cc.wdev.platform.system.commons.domain.chart.PieData;
import cc.wdev.platform.system.core.domain.entity.LoginSessionEntity;
import cc.wdev.platform.system.core.domain.request.LoginSessionSearchRequest;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author elvea
 */
public interface LoginSessionService extends CachingEntityService<LoginSessionEntity, Long> {

    /**
     * 查找用户会话列表
     */
    Page<LoginSessionEntity> findLoginSessionList(LoginSessionSearchRequest request);

    /**
     * 获取单条会话记录
     */
    LoginSessionEntity findBySessionId(String sessionId);

    /**
     * 用户会话终端来源饼图
     */
    ChartVo getPlatformPieChart(Integer type, String dateTime);

    /**
     * 用户会话访问来源折线图
     */
    ChartVo getPlatformLineChart(Integer type, String dateTime, boolean goHeavy);

    /**
     * 获取对应终端登录数
     */
    List<DayAndCount> getCountByPeriod(Integer type, Integer year, Integer month, Integer day, boolean goHeavy);

    /**
     * 获取对应终端登录数
     */
    List<PieData> getCountByPlatform(Integer year, Integer month, Integer day);

    /**
     * 获取在线用户数
     */
    Integer getOnlineUserCount();

    /**
     * 获取单位时间内的登录人次
     */
    long getAllLoginCount(LocalDateTime startTime, LocalDateTime endTime);
}
