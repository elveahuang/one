package cc.wdev.platform.system.commons.api;

import cc.wdev.platform.system.commons.domain.vo.InitializeVo;
import cc.wdev.platform.system.commons.domain.vo.PageVo;

/**
 * @author elvea
 */
public interface CoreApi {

    /**
     * 获取应用初始配置
     */
    InitializeVo initialize();

    /**
     * 获取页面静态页面
     */
    PageVo getPage(String code);

    /**
     * 检测是否允许访问
     */
    boolean checkAccessLimit();

    /**
     * 系统初始化
     */
    void setup();

}
