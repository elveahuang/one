package cc.wdev.platform.commons.extensions.parser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParseConfig implements Serializable {

    private boolean enabled;

    @Builder.Default
    private Debug debug = Debug.builder().build();

    @Builder.Default
    private Tesseract tesseract = Tesseract.builder().build();

    @Builder.Default
    private Document document = Document.builder().build();

    @Builder.Default
    private Media media = Media.builder().build();

    @Data
    @Builder
    public static class Debug implements Serializable {

        /**
         * 是否开启调试模式
         */
        @Builder.Default
        private boolean enabled = false;

    }

    @Data
    @Builder
    public static class Tesseract {

        @Builder.Default
        private String data = "";

        /**
         * 识别语言（Tesseract 语言包，多个用 + 连接，如 chi_sim+eng）
         */
        @Builder.Default
        private String language = "chi_sim+eng";

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document {

        @Builder.Default
        private long maxFileSize = 50L * 1024 * 1024;

        @Builder.Default
        private long maxTextLength = 2_000_000L;

        @Builder.Default
        private boolean ocrEnabled = true;

        @Builder.Default
        private boolean extractEmbedded = true;

        @Builder.Default
        private int timeoutSeconds = 60;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Media {

        @Builder.Default
        private long maxFileSize = 2L * 1024 * 1024 * 1024;

        @Builder.Default
        private long maxDurationSeconds = 7200L;

        @Builder.Default
        private int targetSampleRate = 16000;

        @Builder.Default
        private int targetChannels = 1;

        @Builder.Default
        private String targetFormat = "wav";

    }

}
