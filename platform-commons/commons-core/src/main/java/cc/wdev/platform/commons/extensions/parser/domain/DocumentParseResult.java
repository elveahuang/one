package cc.wdev.platform.commons.extensions.parser.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentParseResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String text;

    private Map<String, String> metadata;

    private Integer pageCount;

    private Boolean truncated;

}
