package cc.wdev.platform.system.core.api;

import cc.wdev.platform.commons.constants.SecurityConstants;
import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.*;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.extensions.captcha.request.CaptchaCheckRequest;
import cc.wdev.platform.commons.extensions.captcha.service.CaptchaService;
import cc.wdev.platform.commons.security.domain.OtpUser;
import cc.wdev.platform.commons.security.domain.SocialUser;
import cc.wdev.platform.commons.utils.*;
import cc.wdev.platform.system.commons.constants.SymbolConstants;
import cc.wdev.platform.system.core.domain.converter.AuthorityConverter;
import cc.wdev.platform.system.core.domain.converter.RoleConverter;
import cc.wdev.platform.system.core.domain.converter.UserConverter;
import cc.wdev.platform.system.core.domain.dto.*;
import cc.wdev.platform.system.core.domain.entity.*;
import cc.wdev.platform.system.core.domain.form.*;
import cc.wdev.platform.system.core.domain.request.*;
import cc.wdev.platform.system.core.domain.vo.InviteStatisticVo;
import cc.wdev.platform.system.core.domain.vo.UserForgetPasswordVo;
import cc.wdev.platform.system.core.domain.vo.UserInfoVo;
import cc.wdev.platform.system.core.domain.vo.UserSimpleInfoVo;
import cc.wdev.platform.system.core.service.*;
import cc.wdev.platform.system.storage.api.AttachmentApi;
import cc.wdev.platform.system.storage.domain.request.AttachmentRelationRequest;
import cc.wdev.platform.system.storage.domain.request.AttachmentRequest;
import cc.wdev.platform.system.storage.domain.vo.AttachmentFileVo;
import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import cc.wdev.platform.system.storage.enums.AttachmentBizTypeEnum;
import cc.wdev.platform.system.storage.enums.AttachmentRelationBizTypeEnum;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.constants.SecurityConstants.ROOT_AUTHORITY;
import static cc.wdev.platform.commons.constants.SecurityConstants.ROOT_USER;
import static cc.wdev.platform.commons.utils.SecurityUtils.sortAuthorities;

/**
 * @author elvea
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserApiImpl implements UserApi {

    private final AttachmentApi attachmentApi;

    private final InviteStatisticApi inviteStatisticApi;

    private final EntityOpenIdService entityOpenIdService;

    private final UserService userService;

    private final AuthorityService authorityService;

    private final RoleService roleService;

    private final UserRoleService userRoleService;

    private final CaptchaService captchaService;

    private final TenantService tenantService;

    /**
     * @see UserApi#search(UserSearchRequest)
     */
    @Override
    public Page<UserSimpleInfoVo> search(UserSearchRequest request) {
        Page<UserEntity> page = userService.search(request);
        List<UserSimpleInfoVo> items = page.getContent().stream().map(UserConverter.INSTANCE::entityToSimpleVo).toList();
        return SpringDataUtils.toSpringDataPage(page.getPageable(), items, page.getTotalElements());
    }

    @Override
    public Page<UserInfoDto> findUserPage(UserSearchRequest request) {
        Page<UserEntity> page = userService.findUserPage(request);
        List<UserInfoDto> items = page.getContent().stream().map(entity -> {
            UserInfoDto dto = UserConverter.INSTANCE.entity2BaseUserInfoDtoNotHaveEmail(entity);
            dto.setRoleIds(new ArrayList<>(userRoleService.findRoleIdsByUserId(entity.getId())));
            return dto;
        }).toList();
        return SpringDataUtils.toSpringDataPage(page.getPageable(), items, page.getTotalElements());
    }

    /**
     * @see UserApi#findUserList(List)
     */
    @Override
    public List<UserSimpleInfoVo> findUserList(List<Long> ids) {
        List<UserEntity> entities = this.userService.findCacheByIds(ids);
        return entities.stream().map(UserConverter.INSTANCE::entityToSimpleVo).toList();
    }

    /**
     * @see UserApi#check(UserCheckRequest)
     */
    @Override
    public boolean check(UserCheckRequest request) {
        return this.userService.check(request);
    }

    /**
     * @see UserApi#getUserInfo(Long)
     */
    @Override
    public UserInfoDto getUserInfo(Long id) {
        UserEntity entity = userService.findById(id);
        return getUserInfoDto(entity);
    }

    /**
     * @see UserApi#getUserInfo(String)
     */
    @Override
    public UserInfoDto getUserInfo(String username) {
        UserEntity entity = userService.findByUsername(username);
        return getUserInfoDto(entity);
    }

    /**
     * @see UserApi#getBaseUserInfo(String)
     */
    @Override
    public UserInfoDto getBaseUserInfo(String username) {
        UserEntity entity = userService.findByUsername(username);
        return getBaseUserInfoDto(entity);
    }

    @Override
    public UserInfoDto getBaseUserInfo(Long userId) {
        UserEntity entity = userService.findById(userId);
        return getBaseUserInfoDto(entity);
    }

    @Override
    public Map<Long, UserInfoVo> batchUserInfo(Collection<Long> userIds) {
        List<UserEntity> entities = userService.findCacheByIds(userIds);
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyMap();
        }
        Map<Long, List<RoleEntity>> roleMap = roleService.batchRole(userIds);
        // 批量获取头像
        Map<Long, AttachmentVo> attachmentMap = attachmentApi.getAttachmentBatch(AttachmentRequest.builder()
            .bizType(AttachmentRelationBizTypeEnum.USER_AVATAR.getValue())
            .bizIdList(userIds)
            .relationBizType(AttachmentRelationBizTypeEnum.USER_AVATAR.getValue())
            .build());
        return entities.stream().map(entity -> {
            UserInfoVo dto = UserConverter.INSTANCE.entityToInfoVo(entity);
            List<RoleEntity> roles = roleMap.get(entity.getId());
            if (CollectionUtils.isNotEmpty(roles)) {
                dto.setRoles(roles.stream().map(RoleEntity::getCode).toList());
            }
            AttachmentVo avatar = attachmentMap.get(entity.getId());
            if (avatar != null) {
                dto.setAvatar(avatar);
                if (CollectionUtils.isEmpty(dto.getAvatar().getFiles()) && StringUtils.isNotBlank(dto.getAvatarUrl())) {
                    dto.getAvatar().setFiles(List.of(AttachmentFileVo.builder().url(dto.getAvatarUrl()).build()));
                }
            }
            return dto;
        }).collect(Collectors.toMap(UserInfoVo::getId, (e) -> e));
    }

    /**
     * @see UserApi#findByUsername(String)
     */
    @Override
    public UserLoginDto findByUsername(String username) {
        UserEntity entity = userService.findByUsername(username);
        return getUserLoginDto(entity);
    }

    /**
     * @see UserApi#findByMobile(String, String)
     */
    @Override
    public UserLoginDto findByMobile(String mobileCountryCode, String mobileNumber) {
        UserEntity entity = userService.findByMobile(mobileCountryCode, mobileNumber);
        return getUserLoginDto(entity);
    }

    /**
     * @see UserApi#findByEmail(String)
     */
    @Override
    public UserLoginDto findByEmail(String email) {
        UserEntity entity = userService.findByEmail(email);
        return getUserLoginDto(entity);
    }

    private UserInfoDto getUserInfoDto(UserEntity entity) {
        if (!ObjectUtils.isValidId(entity)) {
            return null;
        }

        UserInfoDto user = UserConverter.INSTANCE.entity2UserInfoDto(entity);

        // 邀请码
        user.setInviteCode(inviteStatisticApi.generateInviteCode(InviteStatisticRequest.builder()
            .userId(entity.getId())
            .build()));

        // 查询用户所有权限和角色信息
        UserAuthorityDto userAuthorityDto = this.getUserAuthority(user.getId());

        List<String> authorities = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(userAuthorityDto.getGrantedAuthorities())) {
            authorities.addAll(userAuthorityDto.getGrantedAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        }
        user.setAuthorities(authorities);

        List<String> roles = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(userAuthorityDto.getRoles())) {
            roles.addAll(userAuthorityDto.getRoles().stream().map(RoleDto::getCode).toList());
        }
        user.setRoles(roles);

        user.setAvatar(attachmentApi.getAttachment(AttachmentRequest.builder()
            .relationBizType(AttachmentRelationBizTypeEnum.USER_AVATAR.getValue())
            .bizType(AttachmentBizTypeEnum.USER_AVATAR.getValue())
            .bizId(entity.getId())
            .build()));
        if (CollectionUtils.isEmpty(user.getAvatar().getFiles()) && StringUtils.isNotBlank(user.getAvatarUrl())) {
            user.getAvatar().setFiles(List.of(AttachmentFileVo.builder().url(user.getAvatarUrl()).build()));
        }
        return user;
    }

    private UserInfoDto getBaseUserInfoDto(UserEntity entity) {
        if (ObjectUtils.isEmpty(entity)) {
            return null;
        }
        UserInfoDto user = UserConverter.INSTANCE.entity2BaseUserInfoDto(entity);
        user.setAvatar(attachmentApi.getAttachment(AttachmentRequest.builder()
            .relationBizType(AttachmentRelationBizTypeEnum.USER_AVATAR.getValue())
            .bizType(AttachmentBizTypeEnum.USER_AVATAR.getValue())
            .bizId(entity.getId())
            .build()));
        if (CollectionUtils.isEmpty(user.getAvatar().getFiles()) && StringUtils.isNotBlank(user.getAvatarUrl())) {
            user.getAvatar().setFiles(List.of(AttachmentFileVo.builder().url(user.getAvatarUrl()).build()));
        }
        return user;
    }

    private UserLoginDto getUserLoginDto(UserEntity entity) {
        if (ObjectUtils.isEmpty(entity)) {
            return null;
        }
        UserLoginDto user = UserConverter.INSTANCE.entity2UserLoginDto(entity);

        // 获取用户权限相关信息
        UserAuthorityDto authorityDto = this.getUserAuthority(entity.getId());
        user.setAuthorities(authorityDto.getAuthorities());
        user.setRoles(authorityDto.getRoles());
        user.setGrantedAuthorities(authorityDto.getGrantedAuthorities());

        return user;
    }

    /**
     * @see UserApi#register(UserRegisterForm)
     */
    @Override
    public R<?> register(UserRegisterForm form) {
        // 检测验证码
        CaptchaCheckRequest captchaCheckRequest = CaptchaCheckRequest.builder()
            .type(CaptchaTypeEnum.EMAIL)
            .email(form.getEmail())
            .key(form.getCaptchaKey())
            .value(form.getCaptchaValue()).build();
        if (!captchaService.check(captchaCheckRequest)) {
            return R.error();
        }
        // 检测用户名是否可用
        if (userService.checkUsername(UserCheckUsernameDto.builder().username(form.getUsername()).build())) {
            return R.fail(ResponseCodeEnum.USER__USERNAME_NOT_AVAILABLE);
        }
        // 检测邮箱是否可用
        if (userService.checkEmail(UserCheckEmailDto.builder().email(form.getEmail()).build())) {
            return R.fail(ResponseCodeEnum.USER__EMAIL_NOT_AVAILABLE);
        }

        // 保存用户
        UserEntity entity = UserEntity.builder()
            .username(form.getUsername())
            .password(SecurityUtils.encode(form.getPassword()))
            .email(form.getEmail())
            .build();
        userService.save(entity);

        // 设置权限
        userRoleService.saveUserRole(entity.getId(), List.of());
        return R.success();
    }

    /**
     * @see UserApi#findUserDetails(Long)
     */
    @Override
    public UserInfoDto findUserDetails(Long id) {
        UserEntity entity = userService.findUserDetails(id);
        UserInfoDto dto = UserConverter.INSTANCE.entity2UserInfoDto(entity);
        Set<Long> roleIds = userRoleService.findRoleIdsByUserId(id);
        if (CollectionUtils.isNotEmpty(roleIds)) {
            dto.setRoleIds(roleIds.stream().toList());
        } else {
            dto.setRoleIds(Collections.emptyList());
        }
        return dto;
    }

    /**
     * @see UserApi#findBySocial(SocialUser)
     */
    @Override
    public UserLoginDto findBySocial(SocialUser socialUser) {
        EntityOpenIdEntity openIdEntity = entityOpenIdService.findEntityByOpenId(socialUser.getSocialType(), socialUser.getOpenId());
        if (openIdEntity == null || !ObjectUtils.isValidId(openIdEntity.getBizId())) {
            return null;
        }
        UserEntity entity = userService.findById(openIdEntity.getBizId());
        return getUserLoginDto(entity);
    }

    /**
     * @see UserApi#registerSocialUser(SocialUser)
     */
    @Override
    public UserLoginDto registerSocialUser(SocialUser socialUser) {
        if (StringUtils.isNotBlank(socialUser.getInviteCode())) {
            // 对邀请码做一个校验
            InviteStatisticVo statistic = inviteStatisticApi.getByCode(socialUser.getInviteCode());
            if (statistic != null) {
                socialUser.setInviteBy(statistic.getUserId());
            } else {
                socialUser.setInviteCode(SymbolConstants.EMPTY);
                socialUser.setInviteBy(0L);
            }
        }
        UserEntity entity = userService.registerSocialUser(socialUser);
        entityOpenIdService.saveOpenId(entity.getId(), socialUser.getSocialType(), socialUser);
        return getUserLoginDto(entity);
    }

    /**
     * @see UserApi#findByOtp(OtpUser)
     */
    @Override
    public UserLoginDto findByOtp(OtpUser otpUser) {
        OtpTypeEnum otpTypeEnum = BaseEnum.getEnumByValue(otpUser.getOtpType(), OtpTypeEnum.class);
        if (OtpTypeEnum.SMS.equals(otpTypeEnum) && StringUtils.isNotEmpty(otpUser.getMobileNumber())) {
            return this.findByMobile(otpUser.getMobileCountryCode(), otpUser.getMobileNumber());
        } else if (OtpTypeEnum.EMAIL.equals(otpTypeEnum) && StringUtils.isNotEmpty(otpUser.getEmail())) {
            return this.findByEmail(otpUser.getMobileNumber());
        }
        return null;
    }

    /**
     * @see UserApi#registerOtpUser(OtpUser)
     */
    @Override
    public UserLoginDto registerOtpUser(OtpUser smsUser) {
        if (StringUtils.isNotBlank(smsUser.getInviteCode())) {
            // 对邀请码做一个校验
            InviteStatisticVo statistic = inviteStatisticApi.getByCode(smsUser.getInviteCode());
            if (statistic != null) {
                smsUser.setInviteBy(statistic.getUserId());
            } else {
                smsUser.setInviteCode(SymbolConstants.EMPTY);
                smsUser.setInviteBy(0L);
            }
        }
        UserEntity entity = userService.registerSmsUser(smsUser);
        return getUserLoginDto(entity);
    }

    /**
     * @see UserApi#getUserAuthority(Long)
     */
    @Override
    public UserAuthorityDto getUserAuthority(Long id) {
        UserAuthorityDto dto = UserAuthorityDto.builder().id(id).build();

        UserEntity user = userService.findById(id);
        if (ObjectUtils.isEmpty(user)) {
            return dto;
        }

        List<AuthorityEntity> authorityEntityList = authorityService.findByUserId(user.getId());
        // 系统权限
        if (CollectionUtils.isNotEmpty(authorityEntityList)) {
            dto.setAuthorities(AuthorityConverter.INSTANCE.entityListToDtoList(authorityEntityList));
        }
        // 系统角色
        List<RoleEntity> roleEntityList = roleService.findByUserId(user.getId());
        if (CollectionUtils.isNotEmpty(roleEntityList)) {
            dto.setRoles(RoleConverter.INSTANCE.entityListToDtoList(roleEntityList));
        }

        // -----------------------------------------------------------------------------------------------------------------------------------------------------
        // 处理最终权限
        // -----------------------------------------------------------------------------------------------------------------------------------------------------

        Set<GrantedAuthority> authorities = Sets.newHashSet();

        // 顶层租户超级管理员
        if (TenantContext.isRootTenant() && ROOT_USER.equalsIgnoreCase(user.getUsername())) {
            authorities.add(new SimpleGrantedAuthority(ROOT_AUTHORITY));
        }

        // 系统权限
        if (CollectionUtils.isNotEmpty(dto.getAuthorities())) {
            authorities.addAll(dto.getAuthorities().stream()
                .map(e -> new SimpleGrantedAuthority(e.getCode()))
                .collect(Collectors.toSet()));
        }

        // 系统角色
        if (CollectionUtils.isNotEmpty(dto.getRoles())) {
            // 角色类型
            authorities.addAll(dto.getRoles().stream()
                .filter(e -> StringUtils.isNotEmpty(e.getBizType()))
                .map(e -> new SimpleGrantedAuthority(SecurityConstants.ROLE_PREFIX + e.getBizType()))
                .collect(Collectors.toSet())
            );
            // 数据范围
            authorities.addAll(dto.getRoles().stream()
                .filter(e -> StringUtils.isNotEmpty(e.getDataScopeType()))
                .map(e -> new SimpleGrantedAuthority(SecurityConstants.DATA_SCOPE_PREFIX + e.getDataScopeType()))
                .collect(Collectors.toSet())
            );
        }
        dto.setGrantedAuthorities(Collections.unmodifiableSet(sortAuthorities(authorities)));

        return dto;
    }

    /**
     * @see UserApi#updateAccount(UserAccountForm)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<?> updateAccount(UserAccountForm userAccountForm) {
        String userName = SecurityUtils.getUsername();
        UserEntity entity = userService.findByUsername(userName);
        if (ObjectUtils.isEmpty(entity)) {
            return R.error();
        }
        UserCheckRequest checkRequest = UserCheckRequest.builder()
            .id(entity.getId())
            .username(userAccountForm.getUsername())
            .build();
        if (!this.check(checkRequest)) {
            throw new ServiceException(ResponseCodeEnum.USER__USERNAME_NOT_AVAILABLE);
        }
        entity.setUsername(userAccountForm.getUsername());
        entity.setDisplayName(userAccountForm.getDisplayName());
        entity.setSex(userAccountForm.getSex());
        entity.setBirthday(userAccountForm.getBirthday());

        AttachmentVo avatar = userAccountForm.getAvatar();
        if (avatar != null
            && CollectionUtils.isNotEmpty(avatar.getFiles())) {
            entity.setAvatarUrl(avatar.getFiles().getFirst().getUrl());
        }
        entity.setSignature(userAccountForm.getSignature());
        userService.updateById(entity);
        attachmentApi.saveAttachmentRelation(AttachmentRelationRequest.builder()
            .relationBizType(AttachmentRelationBizTypeEnum.USER_AVATAR.getValue())
            .bizType(AttachmentBizTypeEnum.USER_AVATAR.getValue())
            .bizId(entity.getId())
            .attachmentIdList(avatar.getIds())
            .build());
        return R.success();
    }

    /**
     * @see UserApi#forgotPassword(ForgotPasswordForm)
     */
    @Override
    public R<UserForgetPasswordVo> forgotPassword(ForgotPasswordForm form) {
        // 检测验证码
        CaptchaCheckRequest captchaCheckRequest = CaptchaCheckRequest.builder()
            .type(CaptchaTypeEnum.EMAIL)
            .email(form.getEmail())
            .key(form.getCaptchaKey())
            .value(form.getCaptchaValue())
            .build();
        if (!captchaService.check(captchaCheckRequest)) {
            return R.error();
        }
        // 检测邮箱和用户名是否存在
        UserEntity entity = userService.findByEmail(form.getEmail());
        if (entity != null) {
            return R.success(UserForgetPasswordVo.builder().email(entity.getEmail()).username(entity.getUsername()).build());
        }
        return R.error();
    }

    /**
     * @see UserApi#resetPassword(ResetPasswordForm)
     */
    @Override
    public R<?> resetPassword(ResetPasswordForm form) {
        // 检测验证码
        CaptchaCheckRequest captchaCheckRequest = CaptchaCheckRequest.builder()
            .type(CaptchaTypeEnum.EMAIL)
            .email(form.getEmail())
            .key(form.getCaptchaKey())
            .value(form.getCaptchaValue())
            .build();
        if (!captchaService.check(captchaCheckRequest)) {
            return R.error();
        }
        // 检测邮箱和用户名是否存在
        UserEntity entity = userService.findByEmail(form.getEmail());
        if (entity != null) {
            entity.setPassword(SecurityUtils.encode(form.getPassword()));
            userService.updateById(entity);
            return R.success();
        }
        return R.error();
    }

    /**
     * @see UserApi#logout()
     */
    @Override
    public R<?> logout() {
        if (SecurityUtils.isAnonymous()) {
            String userName = SecurityUtils.getUsername();
            log.info("Current username - [{}}]", userName);
        }
        return R.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(UserForm form) {
        TenantEntity tenant = tenantService.findById(TenantContext.getTenantId());
        if (tenant.getAccountCount() != 0) {
            Long userCount = userService.getInternalUserCount();
            // 修改账号，考虑status字段是否由停用变成启用（前端直接传空值到数据库会默认为1，也需要判断）
            if (ObjectUtils.isValidId(form.getId()) && (Objects.equals(form.getStatus(), StatusTypeEnum.ON.getValue()) || form.getStatus() == null)) {
                UserEntity userEntity = userService.checkExistsOrFail(form.getId(), ResponseCodeEnum.USER__NOT_PRESENT);
                if (userEntity != null && Objects.equals(userEntity.getStatus(), StatusTypeEnum.OFF.getValue()) && userCount >= tenant.getAccountCount()) {
                    // 启用账号判断是否超过限制，超过则报错
                    throw new ServiceException(ResponseCodeEnum.TENANT__USER_FULL_ERROR);
                }
            }
            // 新增，直接判断新增一个用户会不会超出限制
            if (ObjectUtils.isInvalidId(form.getId()) && userCount + 1 > tenant.getAccountCount()) {
                throw new ServiceException(ResponseCodeEnum.TENANT__USER_FULL_ERROR);
            }
        }
        UserEntity entity = userService.saveUser(form);
        // 保存邀请统计信息
        inviteStatisticApi.initStatistic(InviteStatisticRequest.builder().userId(entity.getId()).build());
        // 设置角色（roleIds 为 null 时不更新）
        if (form.getRoleIds() != null) {
            userRoleService.saveUserRole(entity.getId(), form.getRoleIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Collection<Long> ids) {
        userService.deleteUser(ids);
        inviteStatisticApi.deleteStatistic(InviteStatisticRequest.builder()
            .userIds(ids)
            .build());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void changeInfo(UserChangeInfoRequest request) {
        UserEntity entity = userService.findByUsername(SecurityUtils.getUsername());
        if (entity == null) {
            throw new ServiceException(ResponseCodeEnum.USER__NOT_PRESENT);
        }
        ObjectUtils.copyNotNullProperties(request, entity);
        if (request.getAvatar() != null && CollectionUtils.isNotEmpty(request.getAvatar().getFiles())) {
            entity.setAvatarUrl(request.getAvatar().getFiles().getFirst().getUrl());
            attachmentApi.saveAttachmentRelation(AttachmentRelationRequest.builder()
                .attachmentIdList(request.getAvatar().getIds())
                .relationBizType(AttachmentRelationBizTypeEnum.USER_AVATAR.getValue())
                .bizType(AttachmentBizTypeEnum.USER_AVATAR.getValue())
                .bizId(entity.getId())
                .config(AttachmentBizTypeEnum.USER_AVATAR.getConfig())
                .build());
        }
        userService.updateById(entity);
    }

    /**
     * @see UserApi#getQRCode(String)
     */
    @Override
    public R<?> getQRCode(String targetPath) {
        if (!StringUtils.isNotEmpty(targetPath)) {
            return R.error();
        }
        String generateQRCode = QrCodeUtils.generate(targetPath);
        return R.success(generateQRCode);
    }

    /**
     * @see UserApi#getRegisterCount(UserRegisterCountRequest)
     */
    @Override
    public long getRegisterCount(@NonNull UserRegisterCountRequest request) {
        return this.userService.getRegisterCount(request);
    }

    /**
     * @see UserApi#getCount(UserCountRequest)
     */
    @Override
    public long getCount(@NonNull UserCountRequest request) {
        return this.userService.getCount(request);
    }

    @Override
    public Boolean checkOriPassword(UserOriPasswordCheckRequest request) {
        UserEntity entity = userService.findByUsername(SecurityUtils.getUsername());
        if (entity == null) {
            throw new ServiceException(ResponseCodeEnum.USER__NOT_PRESENT);
        }
        return StrUtil.isAllBlank(entity.getPassword(), request.getOriPassword()) || SecurityUtils.matches(request.getOriPassword(), entity.getPassword());
    }

    @Override
    public void changePassword(UserChangePasswordForm form) {
        userService.changePassword(form);
    }

    @Override
    public void changeUserName(UserChangeUserNameForm form) {
        UserEntity entity = userService.findByUsername(SecurityUtils.getUsername());
        UserCheckRequest checkRequest = UserCheckRequest.builder()
            .id(entity.getId())
            .username(form.getUsername())
            .build();
        if (!this.check(checkRequest)) {
            throw new ServiceException(ResponseCodeEnum.USER__USERNAME_NOT_AVAILABLE);
        }
        // 修改用户名
        entity.setUsername(form.getUsername());
        userService.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeEmail(UserChangeEmailForm form) {
        UserEntity entity = userService.findByUsername(SecurityUtils.getUsername());
        if (entity == null) {
            throw new ServiceException(ResponseCodeEnum.USER__NOT_PRESENT);
        }

        // 校验验证码
        CaptchaCheckRequest captchaCheckRequest = CaptchaCheckRequest.builder()
            .type(CaptchaTypeEnum.EMAIL)
            .email(form.getEmail())
            .key(form.getCaptchaKey())
            .value(form.getCaptchaValue())
            .build();
        if (!captchaService.check(captchaCheckRequest)) {
            throw new ServiceException(ResponseCodeEnum.INVALID_CAPTCHA);
        }
        // 将相同邮箱的账号邮箱置为空值
        UserEntity byEmail = userService.findByEmail(form.getEmail());
        if (byEmail != null) {
            byEmail.setEmail(SymbolConstants.EMPTY);
            userService.updateById(byEmail);
        }

        // 修改邮箱
        entity.setEmail(form.getEmail());
        userService.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePhone(UserChangePhoneForm form) {
        UserEntity entity = userService.findByUsername(SecurityUtils.getUsername());
        if (entity == null) {
            throw new ServiceException(ResponseCodeEnum.USER__NOT_PRESENT);
        }

        // 校验验证码
        CaptchaCheckRequest captchaCheckRequest = CaptchaCheckRequest.builder()
            .type(CaptchaTypeEnum.SMS)
            .mobileCountryCode(form.getMobileCountryCode())
            .mobileNumber(form.getMobileNumber())
            .key(form.getCaptchaKey())
            .value(form.getCaptchaValue())
            .build();
        if (!captchaService.check(captchaCheckRequest)) {
            throw new ServiceException(ResponseCodeEnum.INVALID_CAPTCHA);
        }

        // 将相同邮箱的账号邮箱置为空值
        UserEntity byMobile = userService.findByMobile(form.getMobileCountryCode(), form.getMobileNumber());
        // 避免重复更新两次同一行数据导致乐观锁第二次更新失败的问题
        if (byMobile != null && !Objects.equals(byMobile.getId(), entity.getId())) {
            byMobile.setMobileCountryCode(SymbolConstants.EMPTY);
            byMobile.setMobileNumber(SymbolConstants.EMPTY);
            userService.updateById(byMobile);
        }

        // 修改手机号
        entity.setMobileCountryCode(form.getMobileCountryCode());
        entity.setMobileNumber(form.getMobileNumber());
        userService.updateById(entity);
    }

    @Override
    public long getAllRegisterCount(LocalDateTime startTime, LocalDateTime endTime) {
        return userService.getAllRegisterCount(startTime, endTime);
    }

    @Override
    public long getAllUserCount(LocalDateTime endTime) {
        return userService.getAllUserCount(endTime);
    }

    @Override
    public long getRegisterCountByTenantId(LocalDateTime startTime, LocalDateTime endTime, long tenantId) {
        return userService.getRegisterCountByTenantId(startTime, endTime, tenantId);
    }

    @Override
    public long getInviteRegisterCount(LocalDateTime startTime, LocalDateTime endTime) {
        return userService.getInviteRegisterCount(startTime, endTime);
    }
}
