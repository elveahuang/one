package cc.wdev.platform.system.message.enums;

import cc.wdev.platform.commons.enums.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author elvea
 */
@Getter
@RequiredArgsConstructor
public enum MessageUserTypeEnum implements BaseEnum<Integer> {
    FROM(1, "FROM", "发送人"),
    TO(2, "TO", "接收人"),
    CC(3, "CC", "抄送人"),
    BCC(4, "BCC", "暗抄送人");

    private final Integer value;
    private final String code;
    private final String description;
}
