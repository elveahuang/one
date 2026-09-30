package cc.wdev.console;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ConsoleApplication {

    static void main(String[] args) {
        SpringApplication.run(ConsoleApplication.class, args);
        log.info("The service to end");
    }

}
