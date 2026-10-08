package cc.wdev.platform.system.core.repository;

import cc.wdev.platform.commons.data.mybatis.repository.BaseEntityRepository;
import cc.wdev.platform.system.core.domain.entity.UserEntity;
import cc.wdev.platform.system.core.domain.request.UserCountRequest;
import cc.wdev.platform.system.core.domain.request.UserSearchRequest;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;

/**
 * @author elvea
 */
@Mapper
public interface UserRepository extends BaseEntityRepository<UserEntity, Long> {

    /**
     * 根据条件查询用户数
     */
    long getCount(@NonNull @Param("request") UserCountRequest request);

    /**
     * 获取指定时间段内所有注册用户数
     */
    @InterceptorIgnore(tenantLine = "true")
    long getAllRegisterCount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 获取时间段前所有用户数
     */
    @InterceptorIgnore(tenantLine = "true")
    long getAllUserCount(@Param("endTime") LocalDateTime endTime);

    /**
     * 获取指定租户指定时间段内所有注册用户数
     */
    @InterceptorIgnore(tenantLine = "true")
    long getRegisterCountByTenantId(LocalDateTime startTime, LocalDateTime endTime, long tenantId);

    /**
     * 分页获取带有指定角色的用户
     */
    IPage<UserEntity> searchWithRoles(Page<?> page, @Param("request") UserSearchRequest request);

    /**
     * 获取内部用户数
     */
    Long getInternalUserCount();
}

