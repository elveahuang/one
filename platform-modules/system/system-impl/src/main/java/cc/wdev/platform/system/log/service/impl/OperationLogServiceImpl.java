package cc.wdev.platform.system.log.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.log.domain.entity.OperationLogEntity;
import cc.wdev.platform.system.log.domain.request.OperationLogSearchRequest;
import cc.wdev.platform.system.log.repository.OperationLogRepository;
import cc.wdev.platform.system.log.service.OperationLogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;

/**
 * @author elvea
 * @see OperationLogService
 * @see BaseCachingEntityService
 */
@Slf4j
@AllArgsConstructor
@Service
public class OperationLogServiceImpl
    extends BaseCachingEntityService<OperationLogEntity, Long, OperationLogRepository>
    implements OperationLogService {
    /**
     * 可根据类名或IP查询系统日志
     */
    @Override
    public Page<OperationLogEntity> findOperationLogList(OperationLogSearchRequest request) {
        // 排序字段仅允许白名单内的列，非法列置空后走默认排序
        request.check(List.of("id", "class_name", "className", "request_ip", "requestIp",
            "method_name", "methodName", "created_at", "createdAt"));
        IPage<OperationLogEntity> page = this.lambdaQueryWrapper()
            .and(StringUtils.isNotBlank(request.getQ()), wrapper -> {
                wrapper.like(OperationLogEntity::getClassName, request.getQ())
                    .or()
                    .like(OperationLogEntity::getRequestIp, request.getQ());
            })
            .eq(OperationLogEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .page(getMyBatisPlusPage(request.getPageable()));
        return MyBatisPlusUtils.toSpringDataPage(page);
    }

}
