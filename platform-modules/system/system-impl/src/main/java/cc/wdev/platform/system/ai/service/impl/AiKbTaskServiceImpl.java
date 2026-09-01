package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.ai.enums.AiVectorizationStatus;
import cc.wdev.platform.commons.data.mybatis.service.BaseEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.converter.AiKbTaskConverter;
import cc.wdev.platform.system.ai.domain.entity.AiKbTaskEntity;
import cc.wdev.platform.system.ai.domain.request.AiKbTaskSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiKbTaskVo;
import cc.wdev.platform.system.ai.repository.AiKbTaskRepository;
import cc.wdev.platform.system.ai.service.AiKbTaskService;
import cc.wdev.platform.system.commons.domain.request.GetRequest;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static cc.wdev.platform.commons.utils.StringUtils.nvl;

/**
 * @author elvea
 */
@Slf4j
@Service
public class AiKbTaskServiceImpl
    extends BaseEntityService<AiKbTaskEntity, Long, AiKbTaskRepository>
    implements AiKbTaskService {

    /**
     * @see AiKbTaskService#getKbTask(GetRequest)
     */
    @Override
    public AiKbTaskVo getKbTask(GetRequest request) {
        AiKbTaskEntity entity = this.resolve(request.getId());
        return AiKbTaskConverter.INSTANCE.entity2Vo(entity);
    }

    /**
     * @see AiKbTaskService#findByPage(AiKbTaskSearchRequest)
     */
    @Override
    public Page<AiKbTaskEntity> findByPage(AiKbTaskSearchRequest request) {
        IPage<AiKbTaskEntity> page = this.lambdaQueryWrapper()
            .eq(ObjectUtils.isValidId(request.getKbId()), AiKbTaskEntity::getKbId, request.getKbId())
            .eq(StringUtils.isNotEmpty(request.getTaskType()), AiKbTaskEntity::getTaskType, request.getTaskType())
            .eq(request.getStatus() != null, AiKbTaskEntity::getStatus, request.getStatus())
            .eq(AiKbTaskEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .orderByDesc(AiKbTaskEntity::getId)
            .page(MyBatisPlusUtils.getMyBatisPlusPage(request.getPageable()));
        return MyBatisPlusUtils.toSpringDataPage(page);
    }

    /**
     * @see AiKbTaskService#findDueRetryTasks(LocalDateTime)
     */
    @Override
    @InterceptorIgnore(tenantLine = "true")
    public List<AiKbTaskEntity> findDueRetryTasks(LocalDateTime now) {
        return this.lambdaQueryWrapper()
            .eq(AiKbTaskEntity::getStatus, AiVectorizationStatus.PENDING.getValue())
            .eq(AiKbTaskEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list();
    }

    /**
     * @see AiKbTaskService#findProcessingTimeoutTasks(LocalDateTime, int)
     */
    @Override
    @InterceptorIgnore(tenantLine = "true")
    public List<AiKbTaskEntity> findProcessingTimeoutTasks(LocalDateTime now, int maxProcessingMinutes) {
        return this.lambdaQueryWrapper()
            .eq(AiKbTaskEntity::getStatus, AiVectorizationStatus.PROCESSING.getValue())
            .eq(AiKbTaskEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list();
    }

    /**
     * @see AiKbTaskService#markProcessing(Long)
     */
    @Override
    public void markProcessing(Long id) {
        this.lambdaUpdateWrapper()
            .eq(AiKbTaskEntity::getId, id)
            .set(AiKbTaskEntity::getStatus, AiVectorizationStatus.PROCESSING.getValue())
            .update();
    }

    /**
     * @see AiKbTaskService#markProgress(Long, int, int)
     */
    @Override
    public void markProgress(Long id, int total, int processed) {
        long progress = 0;
        if (processed >= 0 && total > 0) {
            progress = processed / total;
        }
        this.lambdaUpdateWrapper()
            .eq(AiKbTaskEntity::getId, id)
            .set(AiKbTaskEntity::getProgress, progress)
            .update();
    }

    /**
     * @see AiKbTaskService#markCompleted(Long)
     */
    @Override
    public void markCompleted(Long id) {
        this.lambdaUpdateWrapper()
            .eq(AiKbTaskEntity::getId, id)
            .set(AiKbTaskEntity::getStatus, AiVectorizationStatus.COMPLETED.getValue())
            .update();
    }

    /**
     * @see AiKbTaskService#markFailed(Long, String)
     */
    @Override
    public void markFailed(Long id, String error) {
        this.lambdaUpdateWrapper()
            .eq(AiKbTaskEntity::getId, id)
            .set(AiKbTaskEntity::getStatus, AiVectorizationStatus.FAILED.getValue())
            .set(AiKbTaskEntity::getException, nvl(error))
            .update();
    }

    /**
     * @see AiKbTaskService#markPendingForRetry(Long, LocalDateTime)
     */
    @Override
    public void markPendingForRetry(Long id, LocalDateTime nextRetryAt) {
        this.lambdaUpdateWrapper()
            .eq(AiKbTaskEntity::getId, id)
            .set(AiKbTaskEntity::getStatus, AiVectorizationStatus.PENDING.getValue())
            .update();
    }

    /**
     * @see AiKbTaskService#countFailedByKbId(Long)
     */
    @Override
    public long countFailedByKbId(Long kbId) {
        if (!ObjectUtils.isValidId(kbId)) {
            return 0L;
        }
        return this.lambdaQueryWrapper()
            .eq(AiKbTaskEntity::getKbId, kbId)
            .eq(AiKbTaskEntity::getStatus, AiVectorizationStatus.FAILED.getValue())
            .eq(AiKbTaskEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .count();
    }

}
