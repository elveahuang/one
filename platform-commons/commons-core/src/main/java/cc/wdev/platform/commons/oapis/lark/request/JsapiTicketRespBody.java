package cc.wdev.platform.commons.oapis.lark.request;

import com.google.gson.annotations.SerializedName;

/**
 * @author elvea
 */
public class JsapiTicketRespBody {

    @SerializedName("expire_in")
    private int expire;

    @SerializedName("ticket")
    private String ticket;

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

}
