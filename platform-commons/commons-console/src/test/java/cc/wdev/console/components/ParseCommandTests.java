package cc.wdev.console.components;

import cc.wdev.console.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ParseCommandTests extends BaseTest {

    @Autowired
    ParseCommand parseCommand;

    @Test
    public void parseCommandTest() {
        parseCommand.parse();
    }

}
