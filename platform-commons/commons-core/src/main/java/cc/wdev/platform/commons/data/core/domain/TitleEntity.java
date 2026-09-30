package cc.wdev.platform.commons.data.core.domain;

import java.io.Serializable;

/**
 * @author elvea
 */
public interface TitleEntity extends Serializable {

    String DEFAULT_FIELD_NAME = "title";

    String getTitle();

    void setTitle(String code);

}
