package cc.wdev.platform.system.site.domain.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "关键字對象")
public class KeywordVo {
    @Schema(description = "关键字ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @Schema(description = "关键字内容")
    private String content;
    @Schema(description = "最后修改时间")
    private LocalDateTime updatedAt;
}
