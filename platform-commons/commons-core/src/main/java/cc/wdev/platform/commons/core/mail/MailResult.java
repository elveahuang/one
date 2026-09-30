package cc.wdev.platform.commons.core.mail;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
public class MailResult implements Serializable {

    private boolean success;

    private String response;

    private Object data;

    private Exception exception;

}
