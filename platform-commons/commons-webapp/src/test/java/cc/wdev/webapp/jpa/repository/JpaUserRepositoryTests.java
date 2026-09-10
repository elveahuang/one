package cc.wdev.webapp.jpa.repository;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.webapp.BaseTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author elvea
 */
@Slf4j
public class JpaUserRepositoryTests extends BaseTests {

    @Autowired
    JpaUserRepository jpaUserRepository;

    @Test
    public void baseTest() {
        Assertions.assertNotNull(this.jpaUserRepository);
    }

    @Test
    public void tenantTest() {
        TenantContext.setTenantId(2L);
        log.info("tenantTest, tenantId: {}", TenantContext.getTenantId());
        this.jpaUserRepository.findAll();
    }

}
