package cc.wdev.platform.system.core.api;

import cc.wdev.platform.system.core.domain.dto.TenantDto;
import cc.wdev.platform.system.core.domain.vo.TenantVo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.time.LocalDateTime;
import java.util.List;

import static cc.wdev.platform.commons.constants.MappingConstants.EXCHANGE_PREFIX;

/**
 * @author elvea
 */
@HttpExchange(url = EXCHANGE_PREFIX + "/tenant")
public interface TenantApi {

    @GetExchange("/find-by-id")
    TenantDto findById(@Parameter(description = "ID") @RequestParam("id") Long id);

    @GetExchange("/find-by-code")
    TenantDto findByCode(@Parameter(description = "编码") @RequestParam("code") String code);

    @GetExchange("/find-by-domain")
    TenantDto findByDomain(@Parameter(description = "domain") @RequestParam("domain") String domain);

    /**
     * 获取所有租户
     */
    List<TenantVo> findAll();

    /**
     * 获取单位时间内注册的租户数量
     */
    long getAllTenantCount(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 租户列表
     */
    List<TenantVo> filter();

}
