package cc.wdev.platform.commons.extensions.ip;

import cc.wdev.platform.commons.extensions.ip.geoip2.GeoLite;
import cc.wdev.platform.commons.extensions.ip.ip2region.Ip2Region;

/**
 * @author elvea
 */
public class GlobalIpManager {

    private static volatile GeoLite globalGeoLite = new GeoLite();

    private static volatile Ip2Region globalIp2Region = new Ip2Region();

    public static GeoLite getGeoLite() {
        return globalGeoLite;
    }

    public static Ip2Region getIp2Region() {
        return globalIp2Region;
    }

    public static void setGeoLite(GeoLite geoLite) {
        globalGeoLite = geoLite;
    }

    public static void setIp2Region(Ip2Region ip2Region) {
        globalIp2Region = ip2Region;
    }

}
