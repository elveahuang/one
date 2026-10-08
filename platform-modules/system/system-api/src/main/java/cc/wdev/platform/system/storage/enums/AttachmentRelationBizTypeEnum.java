package cc.wdev.platform.system.storage.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 附件业务关联类型
 *
 * @author erden
 */
@Getter
@AllArgsConstructor
public enum AttachmentRelationBizTypeEnum implements BaseEnum<String> {
    USER_AVATAR("USER_AVATAR", "用户头像"),
    BANNER_COVER("BANNER_COVER", "宣传栏电脑端封面"),
    BANNER_MOBILE_COVER("BANNER_MOBILE_COVER", "宣传栏移动端封面"),
    LINK_COVER("LINK_COVER", "友情链接封面"),
    TENANT_COVER("TENANT_COVER", "租户封面"),
    TENANT_PACKAGE_COVER("TENANT_PACKAGE_COVER", "租户套餐封面"),
    KB_ITEM_DOCUMENT("KB_ITEM_DOCUMENT", "知识库文档"),
    NONE("NONE", "NONE");

    private final String value;
    private final String description;
}
