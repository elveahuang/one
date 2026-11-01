package cc.wdev.platform.system.config.controller.webapp;

import cc.wdev.platform.commons.annotations.Anonymous;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.platform.system.config.domain.converter.ConfigConverter;
import cc.wdev.platform.system.config.domain.entity.ConfigEntity;
import cc.wdev.platform.system.config.domain.vo.ConfigVo;
import cc.wdev.platform.system.config.enums.ConfigGroupTypeEnum;
import cc.wdev.platform.system.config.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * @author irving
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "ConfigController", description = "系统配置控制器")
public class ConfigWebController extends AbstractController {

    /**
     * 允许匿名读取的配置分组：仅页面与协议类内容，
     * 避免通过该端点读取 DEV/ACCESS 等敏感配置项。
     */
    private static final Set<String> PUBLIC_GROUP_TYPES = Set.of(
        ConfigGroupTypeEnum.PAGE.getValue(),
        ConfigGroupTypeEnum.AGREEMENT.getValue());

    private final ConfigService configService;

    @Anonymous
    @Operation(summary = "获取用户端页面配置")
    @ApiResponse(description = "获取用户端页面配置")
    @GetMapping(API_V1_PREFIX + "/user/system-settings")
    public R<ConfigVo> systemSettings(
        @Parameter(description = "配置ID") @RequestParam(value = "id", required = false) Long id) {
        if (!ObjectUtils.isValidId(id)) {
            return R.success(null);
        }
        ConfigEntity entity = this.configService.findById(id);
        if (entity == null || !PUBLIC_GROUP_TYPES.contains(entity.getConfigGroupType())) {
            return R.success(null);
        }
        return R.success(ConfigConverter.INSTANCE.entityToDto(entity));
    }

}
