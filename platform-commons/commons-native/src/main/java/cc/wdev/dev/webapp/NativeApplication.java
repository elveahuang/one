package cc.wdev.dev.webapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class NativeApplication {

    static void main(String[] args) {
        SpringApplication.run(NativeApplication.class, args);
        log.info("The service to end");
    }

}
