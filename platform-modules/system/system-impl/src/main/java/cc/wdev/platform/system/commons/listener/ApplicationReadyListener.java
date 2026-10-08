package cc.wdev.platform.system.commons.listener;

import cc.wdev.platform.commons.extensions.keyword.KeywordManager;
import cc.wdev.platform.system.site.domain.entity.KeywordEntity;
import cc.wdev.platform.system.site.service.KeywordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author elvea
 */
@Slf4j
@Component
@AllArgsConstructor
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    private final KeywordManager keywordManager;

    private final KeywordService keywordService;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        log.info("ApplicationReady...");

        // 初始化系统关键字
        keywordManager.initialize(keywordService.findAll().stream().map(KeywordEntity::getContent).toList());
    }

}
