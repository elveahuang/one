package cc.wdev.platform.system.im.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserVo implements Serializable {
    /**
     * 实体ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long uid;
    /**
     * 实体类型
     */
    private String uType;
    /**
     * 姓名
     */
    private String name;
    /**
     * 头像
     */
    private String avatarUrl;
    /**
     * 性别
     */
    private String sex;
}
