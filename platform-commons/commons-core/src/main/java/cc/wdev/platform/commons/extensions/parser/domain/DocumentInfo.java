package cc.wdev.platform.commons.extensions.parser.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文档元信息
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String title;

    private String author;

    private String language;

    private Integer pageCount;

    private String contentType;

}
