package cc.wdev.platform.system.config.controller.webapp;

import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author irving
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "ConfigController", description = "系统配置控制器")
public class ConfigWebController extends AbstractController {
}
