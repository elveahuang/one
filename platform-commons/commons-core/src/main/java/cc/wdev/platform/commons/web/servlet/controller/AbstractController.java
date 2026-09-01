package cc.wdev.platform.commons.web.servlet.controller;

import cc.wdev.platform.commons.core.GlobalContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 顶层抽象控制器
 *
 * @author elvea
 */
public abstract class AbstractController {

    protected GlobalContext context;

    protected String redirect(String url) {
        return String.format("redirect:%s", url);
    }

    protected String forward(String url) {
        return String.format("forward:%s", url);
    }

    @Autowired
    public void setContext(GlobalContext context) {
        this.context = context;
    }

}
