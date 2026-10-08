package cc.wdev.platform.app;

import lombok.extern.slf4j.Slf4j;
import me.ahoo.cosid.spring.boot.starter.actuate.CosIdEndpointAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

/**
 * @author elvea
 */
@Slf4j
@SpringBootApplication(scanBasePackages = {
    "cc.wdev.platform.commons",
    "cc.wdev.platform.base",
    "cc.wdev.platform.security",
    "cc.wdev.platform.system",
    "cc.wdev.platform.app",
}, exclude = {
    DataSourceAutoConfiguration.class,
    CosIdEndpointAutoConfiguration.class
})
@EntityScan(basePackages = {
    "cc.wdev.platform.system.**.entity",
})
@MapperScan(basePackages = {
    "cc.wdev.platform.system.**.repository",
})
public class AppServerApplication {

    static void main(String[] args) {
        SpringApplication.run(AppServerApplication.class, args);
    }

}
