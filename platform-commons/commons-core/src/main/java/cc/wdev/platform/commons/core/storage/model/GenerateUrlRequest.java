package cc.wdev.platform.commons.core.storage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Duration;

/**
 * 文件参数
 *
 * @author elvea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateUrlRequest implements Serializable {

    /**
     * 标识
     */
    private String key;

    /**
     * 有效期
     */
    @Builder.Default
    private Duration expiration = Duration.ofMinutes(30);

}
