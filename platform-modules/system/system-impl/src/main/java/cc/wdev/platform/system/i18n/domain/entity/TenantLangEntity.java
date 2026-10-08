package cc.wdev.platform.system.i18n.domain.entity;

import cc.wdev.platform.commons.data.mybatis.domain.BaseTenantEntity;
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
@TableName("sys_tenant_lang")
public class TenantLangEntity extends BaseTenantEntity {
    /**
     * 语言ID
     */
    private String lang;
}
