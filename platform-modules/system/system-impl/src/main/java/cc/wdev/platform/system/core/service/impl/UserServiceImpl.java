package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.commons.core.cache.CacheKeyGenerator;
import cc.wdev.platform.commons.data.core.domain.IdEntity;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.security.domain.OtpUser;
import cc.wdev.platform.commons.security.domain.SocialUser;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.SecurityUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.core.annotation.TargetUser;
import cc.wdev.platform.system.core.api.UserApi;
import cc.wdev.platform.system.core.cache.UserCacheKeyGenerator;
import cc.wdev.platform.system.core.domain.converter.UserConverter;
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
import cc.wdev.platform.system.core.repository.UserRepository;
import cc.wdev.platform.system.core.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;
import static cc.wdev.platform.commons.enums.ResponseCodeEnum.USER__NOT_PRESENT;
import static cc.wdev.platform.commons.utils.StringUtils.isNotEmpty;

/**
 * @author elvea
 * @see UserService
 * @see BaseCachingEntityService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends BaseCachingEntityService<UserEntity, Long, UserRepository> implements UserService {

    private final UserCacheKeyGenerator cacheKeyGenerator = new UserCacheKeyGenerator();

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public CacheKeyGenerator getCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    /**
     * @see UserService#search(UserSearchRequest)
     */
    @Override
    public Page<UserEntity> search(UserSearchRequest request) {
        IPage<UserEntity> page = this.lambdaQueryWrapper()
            .and(isNotEmpty(request.getQ()), wrapper -> wrapper
                .like(UserEntity::getUsername, request.getQ())
                .or()
                .like(UserEntity::getDisplayName, request.getQ()))
            .eq(request.getIncludeInActive() == null || Objects.equals(Boolean.FALSE, request.getIncludeInActive()), UserEntity::getActive, ActiveTypeEnum.getEnabledValue())
            .page(getMyBatisPlusPage(request));
        return MyBatisPlusUtils.toSpringDataPage(page);
    }

    /**
     * @see UserApi#check(UserCheckRequest)
     */
    @Override
    public boolean check(UserCheckRequest request) {
        if (StringUtils.isEmpty(request.getUsername()) && StringUtils.isEmpty(request.getEmail())) {
            return false;
        }
        LambdaQueryChainWrapper<UserEntity> wrapper = this.lambdaQueryWrapper();
        if (isNotEmpty(request.getUsername())) {
            wrapper.eq(UserEntity::getUsername, request.getUsername().trim());
        } else if (isNotEmpty(request.getEmail())) {
            wrapper.eq(UserEntity::getEmail, request.getEmail().trim());
        } else if (isNotEmpty(request.getMobileNumber())) {
            wrapper.eq(UserEntity::getMobileNumber, request.getMobileNumber().trim());
        } else {
            return false;
        }
        if (request.getId() != null && request.getId() > 0) {
            wrapper.notIn(UserEntity::getId, request.getId());
        }
        return wrapper.count() <= 0;
    }

    /**
     * @see UserService#findUserPage(UserSearchRequest)
     */
    @Override
    public Page<UserEntity> findUserPage(UserSearchRequest request) {
        IPage<UserEntity> page = this.userRepository.searchWithRoles(getMyBatisPlusPage(request), request);
        return MyBatisPlusUtils.toSpringDataPage(page);
    }

    /**
     * @see UserService#findUserDetails(Long)
     */
    @Override
    public UserEntity findUserDetails(Long id) {
        return this.checkExistsOrFail(id, USER__NOT_PRESENT);
    }

    /**
     * @see UserService#checkUsername(UserCheckUsernameDto)
     */
    @Override
    public boolean checkUsername(UserCheckUsernameDto dto) {
        return lambdaQueryWrapper().eq(UserEntity::getUsername, dto.getUsername()).exists();
    }

    /**
     * @see UserService#checkEmail(UserCheckEmailDto)
     */
    @Override
    public boolean checkEmail(UserCheckEmailDto dto) {
        return lambdaQueryWrapper().eq(UserEntity::getEmail, dto.getEmail()).exists();
    }

    /**
     * @see UserService#checkMobile(UserCheckMobileDto)
     */
    @Override
    public boolean checkMobile(UserCheckMobileDto dto) {
        return lambdaQueryWrapper().eq(UserEntity::getMobileNumber, dto.getMobileNumber()).exists();
    }

    /**
     * @see UserService#findByUsername(String)
     */
    @Override
    public UserEntity findByUsername(String username) {
        return this.findByCacheKey(cacheKeyGenerator.byUsername(username), _ -> this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(UserEntity::getUsername, username)
            .eq(UserEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
        ));
    }

    /**
     * @see UserService#findByUsername(String)
     */
    @Override
    public UserEntity findById(Long id) {
        return this.findByCacheKey(cacheKeyGenerator.byId(id), _ -> this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(UserEntity::getId, id)
            .eq(UserEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
        ));
    }

    /**
     * @see UserService#findByEmail(String)
     */
    @Override
    public UserEntity findByEmail(String email) {
        return this.findByCacheKey(cacheKeyGenerator.byEmail(email), _ -> this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(UserEntity::getEmail, email)
            .eq(UserEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
        ));
    }

    /**
     * @see UserService#findByMobile(String, String)
     */
    @Override
    public UserEntity findByMobile(String mobileCountryCode, String mobileNumber) {
        return this.findByCacheKey(cacheKeyGenerator.byMobile(mobileCountryCode, mobileNumber), _ -> this.findOneByWrapper(this.lambdaQueryWrapper()
            .eq(UserEntity::getMobileNumber, mobileNumber)
            .eq(UserEntity::getMobileCountryCode, mobileCountryCode)
            .eq(UserEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
        ));
    }

    /**
     * @see UserService#getSystemAdministrator()
     */
    @Override
    public UserEntity getSystemAdministrator() {
        return this.findCacheById(1L);
    }

    /**
     * @see UserService#saveUser(UserForm)
     */
    @Override
    @TargetUser
    public UserEntity saveUser(UserForm form) {
        UserEntity entity = this.checkExistsOrReturn(form.getId(), new UserEntity(), USER__NOT_PRESENT);
        //编辑操作时，若用户名不等于旧用户名，且新用户名存在于数据库中，则返回错误
        if (form.getId() != null && form.getId() > 0 && StringUtils.isNotEmpty(form.getUsername()) && !form.getUsername().equals(entity.getUsername())
            && this.checkUsername(UserCheckUsernameDto.builder().username(form.getUsername()).build())) {
            throw new ServiceException(ResponseCodeEnum.USER__USERNAME_NOT_AVAILABLE);
        }
        UserConverter.INSTANCE.formToEntity(form, entity);
        entity.setActive(ActiveTypeEnum.ENABLED.getValue());
        this.save(entity);
        return entity;
    }

    @Override
    public void deleteUser(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        this.softDeleteBatchById(ids);
    }

    /**
     * @see UserService#getInternalUserCount()
     */
    @Override
    public Long getInternalUserCount() {
        return this.mapper.getInternalUserCount();
    }

    @Override
    public List<UserEntity> findUserByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return this.findCacheByIds(ids);
    }


    @Override
    @TargetUser
    public UserEntity registerSocialUser(SocialUser socialUser) {
        UserEntity entity = UserConverter.INSTANCE.socialUserToEntity(socialUser);
        String username = generateCode(socialUser.getSocialType());
        entity.setUsername(username);
        entity.setAvatarUrl(socialUser.getHeadImgUrl());
        entity.setDisplayName(socialUser.getNickname());
        entity.setSex(Objects.equals(2, socialUser.getSex()) ? "F" : "M");
        entity.setActive(ActiveTypeEnum.ENABLED.getValue());
        this.save(entity);

        return this.findByUsername(username);
    }

    @Override
    @TargetUser
    public UserEntity registerSmsUser(OtpUser otpUser) {
        UserEntity entity = UserConverter.INSTANCE.otpUserToEntity(otpUser);

        String username = generateCode(otpUser.getOtpType());
        entity.setUsername(username);
        entity.setDisplayName(username);
        entity.setActive(ActiveTypeEnum.ENABLED.getValue());
        entity.setMobileCountryCode(otpUser.getMobileCountryCode());
        entity.setMobileNumber(otpUser.getMobileNumber());
        this.save(entity);

        return this.findByUsername(username);
    }

    @Override
    public void changePassword(UserChangePasswordForm form) {
        UserEntity entity = this.findByUsername(SecurityUtils.getUsername());
        if (entity == null) {
            throw new ServiceException(USER__NOT_PRESENT);
        }

        // 非首次修改：校验原密码 + 新旧密码不能相同
        if (StringUtils.isNotBlank(entity.getPassword())) {
            if (!SecurityUtils.matches(form.getOriPassword(), entity.getPassword())) {
                throw new ServiceException(ResponseCodeEnum.USER__PASSWORD_NOT_MATCH);
            }
            if (SecurityUtils.matches(form.getNewPassword(), entity.getPassword())) {
                throw new ServiceException(ResponseCodeEnum.USER__PASSWORD_SAME_AS_OLD);
            }
        }
        entity.setPassword(passwordEncoder.encode(form.getNewPassword()));
        this.save(entity);
    }

    /**
     * @see UserService#getRegisterCount(UserRegisterCountRequest)
     */
    @Override
    public long getRegisterCount(@NonNull UserRegisterCountRequest request) {
        return lambdaQueryWrapper()
            .gt(ObjectUtils.isNotEmpty(request.getStartTime()), UserEntity::getCreatedAt, request.getStartTime())
            .lt(ObjectUtils.isNotEmpty(request.getEndTime()), UserEntity::getCreatedAt, request.getEndTime())
            .eq(ObjectUtils.isValidId(request.getInviterId()), UserEntity::getInviteBy, SecurityUtils.getUser().getId())
            .eq(UserEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .count();
    }

    /**
     * @see UserService#getCount(UserCountRequest)
     */
    @Override
    public long getCount(@NonNull UserCountRequest request) {
        return this.getMapper().getCount(request);
    }

    @Override
    public long getAllRegisterCount(LocalDateTime startTime, LocalDateTime endTime) {
        return this.mapper.getAllRegisterCount(startTime, endTime);
    }

    @Override
    public long getAllUserCount(LocalDateTime endTime) {
        return this.mapper.getAllUserCount(endTime);
    }

    @Override
    public long getRegisterCountByTenantId(LocalDateTime startTime, LocalDateTime endTime, long tenantId) {
        return this.mapper.getRegisterCountByTenantId(startTime, endTime, tenantId);
    }

    @Override
    public long getInviteRegisterCount(LocalDateTime startTime, LocalDateTime endTime) {
        return lambdaQueryWrapper()
            .gt(ObjectUtils.isNotEmpty(startTime), UserEntity::getCreatedAt, startTime)
            .lt(ObjectUtils.isNotEmpty(endTime), UserEntity::getCreatedAt, endTime)
            .eq(UserEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .gt(UserEntity::getInviteBy, 0)
            .count();
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    public void setCache(UserEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().set(this.cacheKeyGenerator.byId(model.getId()), model);
            }
            if (isNotEmpty(model.getEmail())) {
                getCacheService().set(this.cacheKeyGenerator.byEmail(model.getEmail()), model);
            }
            if (isNotEmpty(model.getMobileCountryCode()) && isNotEmpty(model.getMobileNumber())) {
                getCacheService().set(this.cacheKeyGenerator.byMobile(model.getMobileCountryCode(), model.getMobileNumber()), model);
            }
            if (isNotEmpty(model.getUsername())) {
                getCacheService().set(this.cacheKeyGenerator.byUsername(model.getUsername()), model);
            }
            if (isNotEmpty(model.getUuid())) {
                getCacheService().set(this.cacheKeyGenerator.byUuid(model.getUuid()), model);
            }
        }
    }

    /**
     * @see BaseCachingEntityService#setCache(IdEntity)
     */
    @Override
    @TargetUser
    public UserEntity updateById(UserEntity entity) {
        return super.updateById(entity);
    }

    @Override
    public void deleteCache(UserEntity model) {
        if (!ObjectUtils.isEmpty(model)) {
            if (!ObjectUtils.isEmpty(model.getId())) {
                getCacheService().delete(this.cacheKeyGenerator.byId(model.getId()));
            }
            if (isNotEmpty(model.getEmail())) {
                getCacheService().delete(this.cacheKeyGenerator.byEmail(model.getEmail()));
            }
            if (isNotEmpty(model.getMobileCountryCode()) && isNotEmpty(model.getMobileNumber())) {
                getCacheService().delete(this.cacheKeyGenerator.byMobile(model.getMobileCountryCode(), model.getMobileNumber()));
            }
            if (isNotEmpty(model.getUuid())) {
                getCacheService().delete(this.cacheKeyGenerator.byUuid(model.getUuid()));
            }
            if (isNotEmpty(model.getUsername())) {
                getCacheService().delete(this.cacheKeyGenerator.byUsername(model.getUsername()));
            }
        }
    }

}
