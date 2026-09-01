package cc.wdev.platform.system.storage.controller.system;

import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author elvea
 */
@RestController
@AllArgsConstructor
@Tag(name = "AttachmentAdminController", description = "附件后台管理控制器")
public class AttachmentSystemController extends AbstractController {
}
