package cc.wdev.platform.commons.data.core.domain;

import java.io.Serializable;

/**
 * @author elvea
 */
public interface ActiveEntity extends Serializable {

    String DEFAULT_FIELD_NAME = "active";

    Integer getActive();

    void setActive(Integer active);

}
