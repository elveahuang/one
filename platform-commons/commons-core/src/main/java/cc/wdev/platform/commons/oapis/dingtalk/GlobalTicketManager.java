package cc.wdev.platform.commons.oapis.dingtalk;

import cc.wdev.platform.commons.oapis.dingtalk.cache.LocalCache;
import cc.wdev.platform.commons.oapis.dingtalk.token.TicketManager;

/**
 * @author elvea
 */
public class GlobalTicketManager {

    private static volatile TicketManager globalTicketManager = new TicketManager(LocalCache.getInstance());

    public static TicketManager getTicketManager() {
        return globalTicketManager;
    }

    public static void setTicketManager(TicketManager ticketManager) {
        globalTicketManager = ticketManager;
    }

}
