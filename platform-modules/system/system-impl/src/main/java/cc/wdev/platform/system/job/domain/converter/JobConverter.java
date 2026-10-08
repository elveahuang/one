package cc.wdev.platform.system.job.domain.converter;

import cc.wdev.platform.commons.core.quartz.QuartzJobInfo;
import cc.wdev.platform.system.job.domain.entity.JobEntity;
import cc.wdev.platform.system.job.domain.request.JobAddRequest;
import cc.wdev.platform.system.job.domain.request.JobSaveRequest;
import cc.wdev.platform.system.job.domain.vo.JobVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author Belly
 */
@Mapper
public interface JobConverter {

    JobConverter INSTANCE = Mappers.getMapper(JobConverter.class);

    JobVO JobEntityToJobVO(JobEntity jobEntity);

    JobEntity JobAddRequestToJobEntity(JobAddRequest jobAddRequest);

    JobEntity JobEditRequestToJobEntity(JobSaveRequest jobSaveRequest);

    @Mapping(target = "key", ignore = true)
    @Mapping(target = "scheduleType", ignore = true)
    @Mapping(target = "unit", ignore = true)
    @Mapping(target = "className", ignore = true)
    QuartzJobInfo JobEntityToQuartzJobInfo(JobEntity jobEntity);
}
