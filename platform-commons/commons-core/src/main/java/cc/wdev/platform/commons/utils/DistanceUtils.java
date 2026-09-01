package cc.wdev.platform.commons.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DistanceUtils {

    private static final double EARTH_RADIUS = 6371000; // 米

    /**
     * 计算两点之间距离（米）
     */
    public static double distance(double lat1, double lng1,
                                  double lat2, double lng2) {

        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);

        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
            + Math.cos(radLat1) * Math.cos(radLat2)
            * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}
