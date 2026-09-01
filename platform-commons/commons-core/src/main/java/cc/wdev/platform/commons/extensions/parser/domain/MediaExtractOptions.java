package cc.wdev.platform.commons.extensions.parser.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 音轨抽取参数
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaExtractOptions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Builder.Default
    private int sampleRate = 16000;

    @Builder.Default
    private int channels = 1;

    @Builder.Default
    private long maxDurationSeconds = 0;

}
