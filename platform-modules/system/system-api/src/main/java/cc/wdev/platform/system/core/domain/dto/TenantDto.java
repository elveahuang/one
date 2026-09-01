package cc.wdev.platform.system.core.domain.dto;

import cc.wdev.platform.commons.constants.DateTimeConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "租户Dto")
public class TenantDto implements Serializable {
    @Schema(description = "租户ID")
    private Long id;

    @Schema(description = "租户编码")
    private String code;

    @Schema(description = "租户标题")
    private String title;

    @Schema(description = "是否根租户")
    private Integer rootInd;

    @Schema(description = "到期时间")
    @JsonFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    @DateTimeFormat(pattern = DateTimeConstants.DEFAULT_DATE_TIME_PATTERN)
    private LocalDateTime expirationDate;

    @Schema(description = "租户用户数")
    private Integer accountCount;

    @Schema(description = "租户状态")
    private Integer status;

    @Schema(description = "删除状态")
    private Integer active;
}
