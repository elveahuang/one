package cc.wdev.platform.commons.oapis.facebody;

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
public class FaceBodyResult implements Serializable {
    private boolean success;
    private String url;
    private String image;
    private String source;
    private String target;
    private String resp;
}
