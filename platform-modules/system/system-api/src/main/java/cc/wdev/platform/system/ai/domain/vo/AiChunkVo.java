package cc.wdev.platform.system.ai.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AiChunkVo implements Serializable {

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 分块ID
     */
    private Long chunkId;

    /**
     * 视频开始时间，单位秒
     */
    private Integer startTime;

    /**
     * 视频结束时间，单位秒
     */
    private Integer endTime;

    /**
     * 文档页码
     */
    private Integer pageNo;

    /**
     * 章节标题
     */
    private String sectionTitle;

    /**
     * 章节内容
     */
    private String sectionContent;
}
