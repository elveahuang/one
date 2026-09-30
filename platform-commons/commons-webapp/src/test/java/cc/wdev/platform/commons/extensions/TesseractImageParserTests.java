package cc.wdev.platform.commons.extensions;

import cc.wdev.platform.commons.enums.MediaTypeCategoryEnum;
import cc.wdev.platform.commons.extensions.parser.ParseConfig;
import cc.wdev.platform.commons.extensions.parser.ParseManager;
import cc.wdev.platform.commons.extensions.parser.domain.ParseRequest;
import cc.wdev.platform.commons.extensions.parser.impl.TesseractImageParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author elvea
 */
public class TesseractImageParserTests {

    @Test
    public void categoryTest() {
        TesseractImageParser parser = newParser();
        Assertions.assertEquals(MediaTypeCategoryEnum.IMAGE, parser.category());
    }

    @Test
    public void supportsTest() {
        TesseractImageParser parser = newParser();
        Assertions.assertTrue(parser.supports(ParseRequest.builder()
            .originalFilename("photo.jpg")
            .build()));
        Assertions.assertTrue(parser.supports(ParseRequest.builder()
            .originalFilename("scan.png")
            .build()));
        Assertions.assertFalse(parser.supports(ParseRequest.builder()
            .originalFilename("document.pdf")
            .build()));
        Assertions.assertFalse(parser.supports(ParseRequest.builder()
            .originalFilename("video.mp4")
            .build()));
    }

    private TesseractImageParser newParser() {
        return new TesseractImageParser(new ParseManager(ParseConfig.builder().build()));
    }

}
