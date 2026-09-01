package cc.wdev.platform.commons.extensions.parser.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 媒体探测信息
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String format;

    private Double durationSeconds;

    private Long size;

    private Boolean hasVideo;

    private Boolean hasAudio;

    private String videoCodec;

    private Integer width;

    private Integer height;

    private Double frameRate;

    private String audioCodec;

    private Integer sampleRate;

    private Integer channels;

    private Long bitrate;

}
