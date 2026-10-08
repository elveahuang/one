package cc.wdev.webapp;

import cc.wdev.platform.commons.data.jpa.repository.BaseEntityRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author elvea
 */
@Slf4j
@SpringBootApplication
@EntityScan(basePackages = {
    "cc.wdev.webapp.**.entity",
})
@EnableJpaRepositories(basePackages = {
    "cc.wdev.webapp.**.repository",
}, repositoryBaseClass = BaseEntityRepositoryImpl.class)
public class WebappApplication {

    static void main(String[] args) {
        SpringApplication.run(WebappApplication.class, args);
    }

}
