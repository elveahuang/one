package cc.wdev.webapp.web;

import cc.wdev.platform.commons.web.request.PageRequest;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import cc.wdev.webapp.jpa.service.JpaUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static cc.wdev.webapp.constants.SystemMappingConstants.API_V1_JPA_USER_LIST;

@RestController
@AllArgsConstructor
@Tag(name = "JpaUserController", description = "默认控制器")
public class JpaUserController extends AbstractController {

    private final JpaUserService jpaUserService;

    @GetMapping(API_V1_JPA_USER_LIST)
    public Page<?> index(PageRequest request) {
        return this.jpaUserService.findByPage(request.getPageable());
    }

}
