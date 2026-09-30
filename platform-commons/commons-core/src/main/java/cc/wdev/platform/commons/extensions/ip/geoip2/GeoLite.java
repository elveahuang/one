package cc.wdev.platform.commons.extensions.ip.geoip2;

import cc.wdev.platform.commons.extensions.ip.Ip;
import cc.wdev.platform.commons.extensions.ip.IpManager;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.InetAddress;

/**
 * @author elvea
 */
@Slf4j
public class GeoLite implements IpManager {

    private DatabaseReader reader;

    @Override
    public void init(Resource resource) {
        log.info("GeoLite [{}] init start", resource.getFilename());
        try {
            reader = new DatabaseReader.Builder(resource.getInputStream()).build();
            log.info("GeoLite [{}] init done", resource.getFilename());
        } catch (IOException e) {
            log.info("GeoLite [{}] init failed", resource.getFilename(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Ip search(String ip) throws Exception {
        CountryResponse countryResponse = this.reader.country(InetAddress.getByName(ip));
        CityResponse cityResponse = this.reader.city(InetAddress.getByName(ip));
        return Ip.builder()
            .country(Ip.Country.builder()
                .code(countryResponse.country().isoCode())
                .name(countryResponse.country().name())
                .label(countryResponse.country().names()).build())
            .city(Ip.City.builder()
                .name(cityResponse.city().name())
                .label(cityResponse.city().names())
                .build())
            .build();
    }

}
