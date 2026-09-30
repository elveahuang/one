package cc.wdev.platform.system.site.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_keyword")
public class KeywordEntity extends BaseTenantEntity {
    /**
     * 关键字内容
     */
    private String content;
}
