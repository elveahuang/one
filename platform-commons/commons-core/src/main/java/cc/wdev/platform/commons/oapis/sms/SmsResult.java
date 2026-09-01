package cc.wdev.platform.commons.oapis.sms;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@Builder
public class SmsResult implements Serializable {

    private boolean success;

    private String response;

    private Object data;

    private Exception exception;

}
