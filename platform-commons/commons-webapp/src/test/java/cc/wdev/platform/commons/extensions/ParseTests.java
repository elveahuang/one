package cc.wdev.platform.commons.extensions;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.extensions.parser.ParseManager;
import cc.wdev.platform.commons.extensions.parser.domain.ParseRequest;
import cc.wdev.platform.commons.extensions.parser.domain.ParseResult;
import cc.wdev.platform.commons.extensions.parser.utils.ParseUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

public class ParseTests extends BaseTests {

    @Autowired
    private ParseManager parseManager;

    @Test
    public void checkTest() {
        ParseUtils.check();
    }

    @Test
    public void convertPptTest() {
        ClassPathResource resource = new ClassPathResource("documents/test.ppt");
        ParseRequest request = ParseRequest.builder()
            .resource(resource)
            .build();
        ParseResult result = this.parseManager.parse(request);
        Assertions.assertNotNull(result);
    }

}
