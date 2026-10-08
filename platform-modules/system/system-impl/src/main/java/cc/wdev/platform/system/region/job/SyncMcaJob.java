package cc.wdev.platform.system.region.job;

import cc.wdev.platform.commons.core.quartz.QuartzJob;
import cc.wdev.platform.system.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 同步国家地区
 */
@Slf4j
@RequiredArgsConstructor
public class SyncMcaJob extends QuartzJob {

    private final RegionService regionService;

    @Override
    protected void execute() throws Exception {
        log.info("SyncMcaJob start.");
        regionService.syncMcaData();
        regionService.getRegionGeoJson();
        log.info("SyncMcaJob finish.");
    }

}
