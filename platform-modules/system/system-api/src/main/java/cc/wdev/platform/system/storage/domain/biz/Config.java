package cc.wdev.platform.system.storage.domain.biz;

import cc.wdev.platform.commons.enums.BaseBizTypeConfig;
import cc.wdev.platform.commons.enums.StorageAccessTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * 附件类型配置
 *
 * @author elvea
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Config implements BaseBizTypeConfig {

    @Builder.Default
    @Schema(title = "允许的文件扩展名列表", description = "允许的文件扩展名列表")
    private List<String> extensions = Lists.newArrayList();

    @Builder.Default
    @Schema(title = "允许的文件类型列表", description = "允许的文件类型列表")
    private List<String> fileTypes = Lists.newArrayList();

    @Builder.Default
    @Schema(title = "是否支持多文件上传", description = "是否支持多文件上传")
    private Boolean multiple = false;

    @Builder.Default
    @Schema(title = "权限范围", description = "权限范围")
    private String accessType = StorageAccessTypeEnum.PRIVATE.getValue();
}
