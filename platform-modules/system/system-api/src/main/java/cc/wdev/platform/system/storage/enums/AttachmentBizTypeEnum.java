package cc.wdev.platform.system.storage.enums;

import cc.wdev.platform.commons.enums.BaseBizTypeEnum;
import cc.wdev.platform.system.commons.enums.BizScopeTypeEnum;
import cc.wdev.platform.system.commons.enums.CoreBizGroupTypeEnum;
import cc.wdev.platform.system.storage.domain.biz.Config;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static cc.wdev.platform.system.commons.constants.SystemAttachmentConstants.*;

/**
 * 附件业务类型
 *
 * @author erden
 */
@Getter
@AllArgsConstructor
public enum AttachmentBizTypeEnum implements BaseBizTypeEnum {
    USER_AVATAR("USER_AVATAR", BizScopeTypeEnum.PLATFORM.getCode(), "用户头像", DEFAULT_PUBLIC_IMAGE_CONFIG),
    BANNER_COVER("BANNER_COVER", BizScopeTypeEnum.PLATFORM.getCode(), "宣传栏电脑端封面", DEFAULT_PRIVATE_IMAGE_CONFIG),
    TENANT_COVER("TENANT_COVER", BizScopeTypeEnum.PLATFORM.getCode(), "宣传栏电脑端封面", DEFAULT_PRIVATE_IMAGE_CONFIG),
    TENANT_PACKAGE_COVER("BANNER_MOBILE_COVER", BizScopeTypeEnum.PLATFORM.getCode(), "宣传栏移动端封面", DEFAULT_PRIVATE_IMAGE_CONFIG),
    BANNER_MOBILE_COVER("BANNER_MOBILE_COVER", BizScopeTypeEnum.PLATFORM.getCode(), "宣传栏移动端封面", DEFAULT_PRIVATE_IMAGE_CONFIG),
    LINK_COVER("LINK_COVER", BizScopeTypeEnum.PLATFORM.getCode(), "友情链接封面", DEFAULT_PRIVATE_IMAGE_CONFIG),
    KB_ITEM_DOCUMENT("KB_ITEM_DOCUMENT", BizScopeTypeEnum.SYSTEM.getCode(), "知识库文档", DEFAULT_PRIVATE_IMAGE_CONFIG),
    TEST("TEST", BizScopeTypeEnum.SYSTEM.getCode(), "测试专用", DEFAULT_PUBLIC_IMAGE_CONFIG),
    NONE("NONE", BizScopeTypeEnum.PLATFORM.getCode(), "未指定", DEFAULT_CONFIG);

    private final String value;
    private final String scope;
    private final String description;
    private final Config config;

    @Override
    public String getGroup() {
        return CoreBizGroupTypeEnum.ATTACHMENT_TYPE.getValue().toUpperCase();
    }

}
