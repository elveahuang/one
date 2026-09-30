package cc.wdev.platform.commons.data.mybatis.log;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.Log;

/**
 * @author elvea
 */
@Slf4j
public class MyBatisLog implements Log {

    public MyBatisLog(String clazz) {
        // 初始化日志
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void error(String s, Throwable e) {
        log.error(s, e);
    }

    @Override
    public void error(String s) {
        log.error(s);
    }

    @Override
    public void debug(String s) {
        log.info(s);
    }

    @Override
    public void trace(String s) {
        log.info(s);
    }

    @Override
    public void warn(String s) {
        log.info(s);
    }

}
