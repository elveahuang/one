package cc.wdev.platform.commons.core.log.domain;

import lombok.*;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApplicationLogDto implements Serializable {
    private String type;
    private String action;
    private String details;
    private String exception;
}
