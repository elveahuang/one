package cc.wdev.platform.commons.extensions;

import cc.wdev.platform.commons.extensions.parser.ParseConfig;
import cc.wdev.platform.commons.extensions.parser.ParseManager;
import cc.wdev.platform.commons.extensions.parser.domain.ParseRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 解析器注册与分类判定测试。
 * <p>
 * 仅依赖扩展名/Content-Type 判定，不触发原生库加载，可在无 tessdata / ffmpeg 环境下运行。
 *
 * @author elvea
 */
public class ParseManagerParserTests {

    @Test
    public void parserRegistrationTest() {
        ParseManager manager = new ParseManager(ParseConfig.builder().build());
        manager.afterPropertiesSet();

        Assertions.assertTrue(manager.supports(request("photo.jpg")));
        Assertions.assertTrue(manager.supports(request("scan.png")));
        Assertions.assertTrue(manager.supports(request("report.pdf")));
        Assertions.assertTrue(manager.supports(request("document.docx")));
        Assertions.assertTrue(manager.supports(request("video.mp4")));
        Assertions.assertTrue(manager.supports(request("audio.mp3")));
        Assertions.assertFalse(manager.supports(request("unknown.xyz")));
    }

    private static ParseRequest request(String filename) {
        return ParseRequest.builder()
            .originalFilename(filename)
            .build();
    }

}
