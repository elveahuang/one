package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.core.cache.SimpleCacheKeyGenerator;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.PlatformTypeEnum;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.commons.constants.SystemCacheConstants;
import cc.wdev.platform.system.commons.domain.chart.ChartVo;
import cc.wdev.platform.system.commons.domain.chart.DayAndCount;
import cc.wdev.platform.system.commons.domain.chart.LineData;
import cc.wdev.platform.system.commons.domain.chart.PieData;
import cc.wdev.platform.system.core.domain.entity.LoginSessionEntity;
import cc.wdev.platform.system.core.domain.request.LoginSessionSearchRequest;
import cc.wdev.platform.system.core.repository.LoginSessionRepository;
import cc.wdev.platform.system.core.service.LoginSessionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;

/**
 * @author elvea
 * @see LoginSessionService
 * @see BaseCachingEntityService
 */
@Service
public class LoginSessionServiceImpl extends BaseCachingEntityService<LoginSessionEntity, Long, LoginSessionRepository>
    implements LoginSessionService {

    /**
     * 按年度计算
     */
    public static int SEARCH_BY_YEAR = 4;
    /**
     * 按月份计算
     */
    public static int SEARCH_BY_MONTH = 3;
    /**
     * 按周计算
     */
    public static int SEARCH_BY_WEEK = 2;
    /**
     * 按日期计算
     */
    public static int SEARCH_BY_DAY = 1;

    private final CacheKeyGenerator cacheKeyGenerator = SimpleCacheKeyGenerator.builder().prefix(SystemCacheConstants.LOGIN_SESSION).build();

    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see LoginSessionService#findLoginSessionList(LoginSessionSearchRequest)
     * 根据用户名查询用户在线列表
     */
    @Override
    public Page<LoginSessionEntity> findLoginSessionList(LoginSessionSearchRequest request) {
        request.setUserId(SecurityUtils.getUid());
        IPage<LoginSessionEntity> page = this.lambdaQueryWrapper()
            .and(StringUtils.isNotEmpty(request.getQ()), wrapper -> {
                wrapper.like(LoginSessionEntity::getUsername, request.getQ());
            })
            .page(getMyBatisPlusPage(request.getPageable()));
        return MyBatisPlusUtils.toSpringDataPage(page);
    }

    /**
     * @see LoginSessionService#findBySessionId(String)
     */
    @Override
    public LoginSessionEntity findBySessionId(String sessionId) {
        LoginSessionEntity condition = new LoginSessionEntity();
        condition.setSessionId(sessionId);
        return this.findOneByWrapper(new QueryWrapper<>(condition));
    }

    /**
     * @see LoginSessionService#getPlatformPieChart(Integer, String)
     */
    @Override
    public ChartVo getPlatformPieChart(Integer type, String dateTime) {
        // 年月日
        int year = -1, month = -1, day = -1;

        // 日期转化
        LocalDateTime date = LocalDateTime.now();

        // 根据选择的日期类型配置参数
        if (type == SEARCH_BY_YEAR) {
            year = date.getYear();
        } else if (type == SEARCH_BY_MONTH) {
            year = date.getYear();
            month = date.getMonthValue();
        } else {
            year = date.getYear();
            month = date.getMonthValue();
            day = date.getDayOfMonth();
        }

        // 获取仪表盘数据
        List<PieData> platFormPieDataList = getCountByPlatform(year, month, day);

        //
        List<String> legendList = Lists.newArrayList();
        List<PieData> pieDataList = Lists.newArrayList();
        for (PlatformTypeEnum t : PlatformTypeEnum.values()) {
            // 饼图数据
            PieData pieData = PieData.builder().value(0L).build();
            for (PieData p : platFormPieDataList) {
                if (p.getName().equalsIgnoreCase(t.getValue())) {
                    pieData.setValue(p.getValue());
                    break;
                }
            }

            legendList.add(t.getValue() + " : " + pieData.getValue());

            pieData.setName(t.getValue() + " : " + pieData.getValue());
            pieDataList.add(pieData);
        }

        return ChartVo.builder().pieDataList(pieDataList).legendList(legendList).build();
    }

    /**
     * @see LoginSessionService#getPlatformLineChart(Integer, String, boolean)
     */
    @Override
    public ChartVo getPlatformLineChart(Integer type, String dateTime, boolean goHeavy) {
        // 折线图横轴数
        int last;
        // 年月周日
        int year = -1, month = -1, week = -1, day = -1;

        // 日期转化
        LocalDateTime date = LocalDateTime.now();
        // 根据选择的日期类型配置参数
        if (type == SEARCH_BY_YEAR) {
            year = date.getYear();

            // 按年度计算时，数据是按1-12月份来计算
            last = 12;
        } else if (type == SEARCH_BY_MONTH) {
            year = date.getYear();
            month = date.getMonthValue();

            // 按月份计算时，数据是按当月1号到当月最后一天
            last = date.plusMonths(1).withDayOfMonth(1).minusDays(1).getDayOfMonth();
        }/* else if (type == SEARCH_BY_WEEK) {
            year = date.getYear();
            month = date.getMonthOfYear();
            week = ;

            // 按周计算时，数据是按周一至周日计算
            last = 7;
        } */ else {
            year = date.getYear();
            month = date.getMonthValue();
            day = date.getDayOfMonth();

            // 按日期计算时，数据是按0-24小时计算
            last = 24;
        }

        List<String> legendList = Lists.newArrayList();
        List<String> xAxisDataList = Lists.newArrayList();
        List<LineData> lineDataList = Lists.newArrayList();

        // todo 根据登录平台细分
        /*for (PlatformTypeEnum t : PlatformTypeEnum.values()) {
            // 折线图数据
            LineData lineData = LineData.builder().name(t.getCode()).type("line").build();

            List<DayAndCount> dayAndCount = getCountByPeriod(t.getCode(), year, month, day);
            // 初始化数据
            int[] data = new int[last];
            for (DayAndCount dac : dayAndCount) {
                if (type == SEARCH_BY_DAY) {
                    // 特殊处理，0点的数据，-1会出现数组越界的异常
                    data[dac.getKey()] = dac.getCount();
                } else {
                    data[dac.getKey() - 1] = dac.getCount();
                }
            }
            lineData.setData(data);

            lineDataList.add(lineData);

            legendList.add(t.getCode());
        }*/

        // 折线图数据
        LineData lineData = LineData.builder().name("web").type("line").build();

        List<DayAndCount> dayAndCount = getCountByPeriod(type, year, month, day, goHeavy);
        // 初始化数据
        int[] data = new int[last];
        for (DayAndCount dac : dayAndCount) {
            if (type == SEARCH_BY_DAY) {
                // 特殊处理，0点的数据，-1会出现数组越界的异常
                data[dac.getKey()] = dac.getCount();
            } else {
                data[dac.getKey() - 1] = dac.getCount();
            }
        }
        lineData.setData(data);

        lineDataList.add(lineData);

        legendList.add("web");

        // 填充横轴数据
        for (int i = 1; i <= last; i++) {
            xAxisDataList.add(String.valueOf(i));
        }

        return ChartVo.builder().legendList(legendList).lineDataList(lineDataList).xAxisDataList(xAxisDataList).build();
    }

    /**
     * @see LoginSessionService#getCountByPeriod(Integer, Integer, Integer, Integer, boolean)
     */
    @Override
    public List<DayAndCount> getCountByPeriod(Integer type, Integer year, Integer month, Integer day, boolean goHeavy) {
        return mapper.getCountByPeriod(type, year, month, day, goHeavy);
    }

    /**
     * @see LoginSessionService#getCountByPlatform(Integer, Integer, Integer)
     */
    @Override
    public List<PieData> getCountByPlatform(Integer year, Integer month, Integer day) {
        return mapper.getCountByPlatform(year, month, day);
    }

    /**
     * @see LoginSessionService#getOnlineUserCount()
     */
    @Override
    public Integer getOnlineUserCount() {
        LocalDateTime date = LocalDateTime.now();
        return lambdaQueryWrapper()
            .select(LoginSessionEntity::getUserId)
            .isNull(LoginSessionEntity::getEndDatetime)
            .gt(LoginSessionEntity::getLastAccessDatetime, date)
            .groupBy(LoginSessionEntity::getUserId)
            .list()
            .size();
    }

    @Override
    public long getAllLoginCount(LocalDateTime startTime, LocalDateTime endTime) {
        return this.mapper.getAllLoginCount(startTime, endTime);
    }

}
