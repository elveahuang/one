package cc.wdev.webapp.web;

import cc.wdev.platform.commons.annotations.Anonymous;
import cc.wdev.platform.commons.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Tag(name = "SwaggerController", description = "接口文档控制器")
public class SwaggerController {

    @Anonymous
    @Operation(summary = "首页")
    @ApiResponse(description = "首页")
    @PostMapping("/swagger/home")
    public R<String> home() {
        return R.success("Hello World");
    }

}
