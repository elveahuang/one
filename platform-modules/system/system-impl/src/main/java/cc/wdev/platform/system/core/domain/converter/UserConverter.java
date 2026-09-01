package cc.wdev.platform.system.core.domain.converter;

import cc.wdev.platform.commons.security.domain.OtpUser;
import cc.wdev.platform.commons.security.domain.SocialUser;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.core.domain.dto.UserInfoDto;
import cc.wdev.platform.system.core.domain.dto.UserLoginDto;
import cc.wdev.platform.system.core.domain.entity.UserEntity;
import cc.wdev.platform.system.core.domain.form.UserForm;
import cc.wdev.platform.system.core.domain.vo.UserExportVo;
import cc.wdev.platform.system.core.domain.vo.UserInfoVo;
import cc.wdev.platform.system.core.domain.vo.UserSimpleInfoVo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.Period;

/**
 * @author elvea
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    @Mapping(target = "organizations", ignore = true)
    @Mapping(target = "positions", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "vips", ignore = true)
    UserLoginDto entity2UserLoginDto(UserEntity entity);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "vips", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "roleIds", ignore = true)
    UserInfoDto entity2UserInfoDto(UserEntity entity);

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "vips", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "roleIds", ignore = true)
    UserInfoDto entity2BaseUserInfoDto(UserEntity entity);

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "mobileCountryCode", ignore = true)
    @Mapping(target = "mobileNumber", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "vips", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    UserInfoDto entity2BaseUserInfoDtoNotHaveEmail(UserEntity entity);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "idCardType", ignore = true)
    @Mapping(target = "idCardNo", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "passwordExpireAt", ignore = true)
    @Mapping(target = "passwordErrorAt", ignore = true)
    @Mapping(target = "passwordErrorCount", ignore = true)
    @Mapping(target = "lastLoginStatus", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "signature", ignore = true)
    @Mapping(target = "inviteCode", ignore = true)
    @Mapping(target = "inviteBy", ignore = true)
    @Mapping(target = "telegram", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    UserEntity formToEntity(UserForm form);

    void formToEntity(UserForm formm, @MappingTarget UserEntity entity);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    UserInfoVo entityToInfoVo(UserEntity entity);


    UserSimpleInfoVo entityToSimpleVo(UserEntity entity);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "displayName", source = "nickname")
    @Mapping(target = "name", source = "nickname")
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "mobileCountryCode", ignore = true)
    @Mapping(target = "mobileNumber", ignore = true)
    @Mapping(target = "sex", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "idCardType", ignore = true)
    @Mapping(target = "idCardNo", ignore = true)
    @Mapping(target = "birthday", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "signature", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "passwordExpireAt", ignore = true)
    @Mapping(target = "passwordErrorAt", ignore = true)
    @Mapping(target = "passwordErrorCount", ignore = true)
    @Mapping(target = "telegram", ignore = true)
    @Mapping(target = "lastLoginStatus", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    UserEntity socialUserToEntity(SocialUser socialUser);

    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "displayName", ignore = true)
    @Mapping(target = "sex", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "idCardType", ignore = true)
    @Mapping(target = "idCardNo", ignore = true)
    @Mapping(target = "birthday", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "signature", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "passwordExpireAt", ignore = true)
    @Mapping(target = "passwordErrorAt", ignore = true)
    @Mapping(target = "passwordErrorCount", ignore = true)
    @Mapping(target = "telegram", ignore = true)
    @Mapping(target = "lastLoginStatus", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    UserEntity otpUserToEntity(OtpUser otpUser);

    /**
     *
     */
    void entityToSimpleVo(UserEntity entity, @MappingTarget UserSimpleInfoVo vo);

    @Mapping(source = "birthday", target = "age", qualifiedByName = "calculateAge")
    @Mapping(source = "sex", target = "sex", qualifiedByName = "getSexToText")
    UserExportVo entityToExportVo(UserEntity entity);

    @Named("calculateAge")
    default Integer calculateAge(LocalDate birthday) {
        if (birthday == null) {
            return null;
        }
        return Period.between(birthday, LocalDate.now()).getYears();
    }

    @Named("getSexToText")
    default String getSexToText(String sex) {
        if (StringUtils.isNotBlank(sex) && sex.equals("F")) {
            return "女";
        }
        if (StringUtils.isNotBlank(sex) && sex.equals("M")) {
            return "男";
        }
        return "未指定";
    }

}
