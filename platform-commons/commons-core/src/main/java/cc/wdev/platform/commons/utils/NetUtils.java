package cc.wdev.platform.commons.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

@Slf4j
public abstract class NetUtils {

    private final static String UNKNOWN = "unknown";

    public static boolean isValidIp(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return false;
        }

        try {
            if (UNKNOWN.equalsIgnoreCase(ip)) {
                return false;
            }
            return InetAddress.getByName(ip) != null;
        } catch (Exception e) {
            log.error("Invalid IP address: {}", ip, e);
            return false;
        }
    }

}
