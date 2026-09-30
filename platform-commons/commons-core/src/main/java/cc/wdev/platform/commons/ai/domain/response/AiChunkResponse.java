package cc.wdev.platform.commons.ai.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AiChunkResponse implements Serializable {

    /**
     * 视频开始时间，单位秒
     */
    @Builder.Default
    private Long startTime = 0L;

    /**
     * 视频结束时间，单位秒
     */
    @Builder.Default
    private Long endTime = 0L;

    /**
     * 文档页码
     */
    @Builder.Default
    private Integer pageNo = 0;

    /**
     * 章节标题
     */
    @Builder.Default
    private String sectionTitle = "";

    /**
     * 章节内容
     */
    @Builder.Default
    private String sectionContent = "";
}
