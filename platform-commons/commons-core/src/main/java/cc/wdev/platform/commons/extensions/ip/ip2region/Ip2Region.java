package cc.wdev.platform.commons.extensions.ip.ip2region;

import cc.wdev.platform.commons.extensions.ip.Ip;
import cc.wdev.platform.commons.extensions.ip.IpManager;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @author elvea
 */
@Slf4j
public class Ip2Region implements IpManager {

    private Searcher searcher;

    @Override
    public void init(Resource resource) {
        log.info("Ip2Region [{}] init start", resource.getFilename());

        try {
            Searcher.verifyFromFile(resource.getFile().getAbsolutePath());
        } catch (Exception e) {
            log.info("Ip2Region [{}] verify failed", resource.getFilename(), e);
            return;
        }

        byte[] vIndex;
        try {
            vIndex = Searcher.loadVectorIndexFromFile(resource.getFile().getAbsolutePath());
            log.info("Ip2Region [{}] load vector index done", resource.getFilename());
        } catch (IOException e) {
            log.info("Ip2Region [{}] load vector index failed", resource.getFilename(), e);
            throw new RuntimeException(e);
        }

        try {
            this.searcher = Searcher.newWithVectorIndex(Version.IPv4, resource.getFile().getAbsolutePath(), vIndex);
        } catch (Exception e) {
            log.info("Ip2Region [{}] new vector index failed", resource.getFilename(), e);
        }
    }

    @Override
    public Ip search(String ip) throws Exception {
        String result = this.searcher.search(ip);
        return Ip.builder().details(result).build();
    }

}
