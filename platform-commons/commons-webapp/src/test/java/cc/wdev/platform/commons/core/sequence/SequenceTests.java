package cc.wdev.platform.commons.core.sequence;

import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.webapp.BaseTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
public class SequenceTests extends BaseTests {

    @Autowired
    private Sequence sequence;

    @Test
    public void nextIdTest() {
        Assertions.assertNotNull(this.sequence);
        long id = this.sequence.nextId();
        Assertions.assertTrue(ObjectUtils.isValidId(id));
    }

    @Test
    public void generateCodeTest() {
        Assertions.assertNotNull(this.sequence);
        String code = this.sequence.generateCode();
        Assertions.assertTrue(StringUtils.isNotBlank(code));
    }

}
