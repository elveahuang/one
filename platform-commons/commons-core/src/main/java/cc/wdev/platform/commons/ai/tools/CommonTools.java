package cc.wdev.platform.commons.ai.tools;

import cc.wdev.platform.commons.constants.GlobalConstants;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.annotation.RegisterReflection;

import java.time.LocalDateTime;

/**
 * @author elvea
 */
@RegisterReflection(memberCategories = MemberCategory.INVOKE_DECLARED_METHODS)
public class CommonTools {

    @Tool(name = "getVersion", description = "获取应用版本号")
    public String getVersion() {
        return GlobalConstants.VERSION;
    }

    @Tool(name = "getCurrentDateTime", description = "获取系统当前时间")
    public String getCurrentDateTime() {
        return LocalDateTime.now().toString();
    }

}
