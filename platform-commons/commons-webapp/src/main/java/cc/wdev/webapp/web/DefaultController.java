package cc.wdev.webapp.web;

import cc.wdev.platform.commons.annotations.Anonymous;
import cc.wdev.platform.commons.domain.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static cc.wdev.webapp.constants.SystemMappingConstants.API_V1_HOME;
import static cc.wdev.webapp.constants.SystemMappingConstants.WEB_HOME;

@Controller
@AllArgsConstructor
@Tag(name = "DefaultController", description = "默认控制器")
public class DefaultController {

    @Anonymous
    @ResponseBody
    @PostMapping(API_V1_HOME)
    public R<String> home() {
        return R.success("Hello World");
    }

    @Anonymous
    @GetMapping(WEB_HOME)
    public String index() {
        return "index";
    }

}
