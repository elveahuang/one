package cc.wdev.platform.commons.data.core.domain;

import java.io.Serializable;

/**
 * @author elvea
 */
public interface IdEntity extends Serializable {

    /**
     * @return Long
     */
    Long getId();

    /**
     * @param id Long
     */
    void setId(Long id);

    /**
     * @return String
     */
    String getIdAsString();

}
