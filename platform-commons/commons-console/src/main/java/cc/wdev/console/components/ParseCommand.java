package cc.wdev.console.components;

import cc.wdev.platform.commons.extensions.parser.utils.ParseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ParseCommand {

    @Command(name = "parse")
    public void parse() {
        ParseUtils.check();
    }

}
