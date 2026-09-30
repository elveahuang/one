package cc.wdev.platform.system.commons.constants;

import cc.wdev.platform.system.commons.enums.BaseRoleTypeEnum;
import cc.wdev.platform.system.commons.enums.RoleTypeEnum;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;
import static cc.wdev.platform.system.commons.domain.AuthorityNode.createRoleTypes;

public interface SystemSecurityConstants {

    BaseRoleTypeEnum[] ALL_ROLE_TYPE = RoleTypeEnum.values();

    BaseRoleTypeEnum[] ALL_ADMIN_ROLE_TYPE = createRoleTypes(RoleTypeEnum.PLATFORM_ADMINISTRATOR, RoleTypeEnum.PLATFORM_ADMINISTRATOR);

    BaseRoleTypeEnum[] ONLY_PLATFORM_ADMIN_ROLE_TYPE = createRoleTypes(RoleTypeEnum.PLATFORM_ADMINISTRATOR);

    BaseRoleTypeEnum[] ONLY_SYSTEM_ADMIN_ROLE_TYPE = createRoleTypes(RoleTypeEnum.SYSTEM_ADMINISTRATOR);

    /**
     * =================================================================================================================
     * 忽略权限检查的地址
     * =================================================================================================================
     */

    String[] WEB_EXCLUDE_URLS = {
    };

    String[] API_EXCLUDE_URLS = {
        API_V1_PREFIX + "/initialize",
        API_V1_PREFIX + "/agreement",
        API_V1_PREFIX + "/privacy",
        API_V1_PREFIX + "/captcha/**",
        API_V1_PREFIX + "/oapis/wechat/mp/signature",
        API_V1_PREFIX + "/oapis/wechat/mp/callback",
        API_V1_PREFIX + "/captcha/sms/check",
        API_V1_PREFIX + "/captcha/sms",
        API_V1_PREFIX + "/captcha/mail/check",
        API_V1_PREFIX + "/captcha/mail",
        API_V1_PREFIX + "/captcha/code/check",
        API_V1_PREFIX + "/captcha/code",
        API_V1_PREFIX + "/captcha/code",
        API_V1_PREFIX + "/pages/**",
    };
}
