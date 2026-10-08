package cc.wdev.platform.commons.data.core.domain;

import java.io.Serializable;

/**
 * @author elvea
 */
public interface CodeEntity extends Serializable {

    String DEFAULT_FIELD_NAME = "code";

    String getCode();

    void setCode(String code);

}
