package cc.wdev.dev.webapp.web;

import cc.wdev.dev.webapp.BaseWebTests;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static cc.wdev.webapp.constants.SystemMappingConstants.API_V1_JPA_USER_LIST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author elvea
 */
public class JpaUserControllerTests extends BaseWebTests {

    @Test
    public void baseTest() throws Exception {
        MockHttpServletResponse resp = this.mvc.perform(MockMvcRequestBuilders.get(API_V1_JPA_USER_LIST))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();
        System.out.println(resp.getContentAsString());
    }

}
