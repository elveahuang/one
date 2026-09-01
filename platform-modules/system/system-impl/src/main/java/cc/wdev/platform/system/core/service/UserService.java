package cc.wdev.platform.system.core.service;

import cc.wdev.platform.commons.security.domain.OtpUser;
import cc.wdev.platform.commons.security.domain.SocialUser;
import cc.wdev.platform.commons.service.CachingEntityService;
import cc.wdev.platform.commons.service.EntityService;
import cc.wdev.platform.system.core.domain.dto.UserCheckEmailDto;
import cc.wdev.platform.system.core.domain.dto.UserCheckMobileDto;
import cc.wdev.platform.system.core.domain.dto.UserCheckUsernameDto;
import cc.wdev.platform.system.core.domain.entity.UserEntity;
import cc.wdev.platform.system.core.domain.form.UserChangePasswordForm;
import cc.wdev.platform.system.core.domain.form.UserForm;
import cc.wdev.platform.system.core.domain.request.UserCheckRequest;
import cc.wdev.platform.system.core.domain.request.UserCountRequest;
import cc.wdev.platform.system.core.domain.request.UserRegisterCountRequest;
import cc.wdev.platform.system.core.domain.request.UserSearchRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * @author elvea
 * @see EntityService
 */
public interface UserService extends CachingEntityService<UserEntity, Long> {

    /**
     * 搜索用户
     */
    Page<UserEntity> search(UserSearchRequest request);

    /**
     * 检查用户名是否可用
     */
    boolean check(UserCheckRequest request);

    /**
     * 批量获取用户
     */
    Page<UserEntity> findUserPage(UserSearchRequest request);

    /**
     * 获取用户详情
     */
    UserEntity findUserDetails(Long id);

    /**
     * 根据username检查用户是否存在
     */
    boolean checkUsername(UserCheckUsernameDto dto);

    /**
     * 根据邮箱查看用户是否存在
     */
    boolean checkEmail(UserCheckEmailDto dto);

    /**
     * 根据用户手机查找用户是否存在
     */
    boolean checkMobile(UserCheckMobileDto dto);

    /**
     * 根据用户名查找用户
     */
    UserEntity findByUsername(String username);

    /**
     * 根据id查找用户
     */
    UserEntity findById(Long id);

    /**
     * 根据邮箱查找用户
     */
    UserEntity findByEmail(String email);

    /**
     * 根据手机查找用户
     */
    UserEntity findByMobile(String mobileCountryCode, String mobileNumber);

    /**
     * 获取系统管理员
     */
    UserEntity getSystemAdministrator();

    /**
     * 保存用户
     */
    UserEntity saveUser(UserForm form);

    /**
     * 删除用户
     */
    void deleteUser(Collection<Long> ids);

    /**
     * 获取内部用户数
     */
    Long getInternalUserCount();

    /**
     * 批量获取用户
     */
    List<UserEntity> findUserByIds(List<Long> ids);

    /**
     * 注册用户
     */
    UserEntity registerSocialUser(SocialUser socialUser);

    /**
     * 注册用户
     */
    UserEntity registerSmsUser(OtpUser smsUser);

    /**
     * 修改密码
     */
    void changePassword(UserChangePasswordForm form);

    /**
     * 获取时间段内全站的注册人数
     */
    long getRegisterCount(@NonNull UserRegisterCountRequest request);

    /**
     * 查询用户数
     */
    long getCount(@NonNull UserCountRequest request);

    /**
     * 获取时间段内全站的注册人数
     */
    long getAllRegisterCount(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取结束时间前全站的用户数
     */
    long getAllUserCount(LocalDateTime endTime);

    /**
     * 获取时间段内租户的注册人数
     */
    long getRegisterCountByTenantId(LocalDateTime startTime, LocalDateTime endTime, long tenantId);

    /**
     * 获取时间段内邀请注册的用户数
     */
    long getInviteRegisterCount(LocalDateTime startTime, LocalDateTime endTime);
}
