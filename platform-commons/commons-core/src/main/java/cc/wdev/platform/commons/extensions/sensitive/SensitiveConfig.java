package cc.wdev.platform.commons.extensions.sensitive;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author elvea
 */
@Slf4j
@Getter
@Setter
@Builder
public class SensitiveConfig implements Serializable {

    private String aesKey;

}
