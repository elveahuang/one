package cc.wdev.platform.system.i18n.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.SimpleEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author erden
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_lang")
public class LangEntity extends SimpleEntity {
    /**
     * 编号
     */
    private String code;
    /**
     * 语言编码
     */
    private String lang;
    /**
     * 地区编码
     */
    private String country;
    /**
     * 文本
     */
    private String label;
    /**
     * 备注
     */
    private String description;
    /**
     * 默认语言
     */
    private Integer defaultInd;
}
