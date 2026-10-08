package cc.wdev.platform.commons.extensions.ip;

import org.springframework.core.io.Resource;

/**
 * @author elvea
 */
public interface IpManager {

    void init(Resource resource);

    Ip search(String ip) throws Exception;

}
