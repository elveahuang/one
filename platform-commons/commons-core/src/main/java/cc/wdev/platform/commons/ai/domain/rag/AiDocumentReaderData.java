package cc.wdev.platform.commons.ai.domain.rag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AiDocumentReaderData implements Serializable {
    /**
     * 类型
     */
    private String type;
    /**
     * 文本内容
     */
    private String text;
    /**
     * 元数据
     */
    private Map<String, Object> metadata;
}
