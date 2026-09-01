package cc.wdev.dev.webapp.web;

import cc.wdev.dev.webapp.BaseWebTests;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author elvea
 */
public class DefaultControllerTests extends BaseWebTests {

    @Test
    public void baseTest() throws Exception {
        MockHttpServletResponse resp = this.mvc.perform(MockMvcRequestBuilders.post("/"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();
        System.out.println(resp.getContentAsString());
    }

}
