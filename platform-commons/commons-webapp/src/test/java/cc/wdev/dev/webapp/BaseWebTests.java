package cc.wdev.dev.webapp;

import cc.wdev.webapp.WebappApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * @author elvea
 */
@Slf4j
@Configuration
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = {WebappApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public abstract class BaseWebTests {

    protected MockMvc mvc;

    protected WebTestClient client;

    protected MockMvcTester tester;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
        client = MockMvcWebTestClient.bindToApplicationContext(wac).build();
        tester = MockMvcTester.from(wac);
    }

}
