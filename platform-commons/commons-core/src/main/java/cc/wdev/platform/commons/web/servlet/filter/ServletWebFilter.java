package cc.wdev.platform.commons.web.servlet.filter;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.JacksonUtils;
import cc.wdev.platform.commons.utils.WebServletUtils;
import cc.wdev.platform.commons.utils.mdc.MdcContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author elvea
 */
@Slf4j
public class ServletWebFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (WebServletUtils.isInternalRequest(request)) {
            chain.doFilter(request, response);
        } else {
            String requestUrl = request.getRequestURI();
            String serverName = request.getServerName();

            long startTime, endTime, totalTime = 0;
            try {
                // 初始化MDC上下文
                MdcContext.handleServletRequest(request, response);

                log.info("[ServletWebFilter] doFilter [{}] Start. URL [{}]. Server Name [{}]", MdcContext.getRequestId(), requestUrl, serverName);

                // 初始化租户上下文
                TenantContext.handleServletRequest(request, response);

                // 记录执行时间
                startTime = System.currentTimeMillis();
                chain.doFilter(request, response);
                endTime = System.currentTimeMillis();
                totalTime = endTime - startTime;
            } catch (ServiceException e) {
                // 这里在过滤器里面处理租户问题的异常，由于执行时间早于全局异常处理器，所以要自己用json反序列化抛出来
                log.error("[ServletWebFilter] Tenant exception: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json;charset=UTF-8");
                R<?> fail = R.fail(e.getResponseCode());
                try {
                    response.getWriter().write(JacksonUtils.toJson(fail));
                } catch (Exception ex) {
                    throw new RuntimeException();
                }
            } finally {
                log.info("[ServletWebFilter] doFilter [{}] End. URL [{}]. Server Name [{}]. Total Time [{}]", MdcContext.getRequestId(), requestUrl, serverName, totalTime);

                // 清理租户上下文
                TenantContext.clear();
                // 清理MDC上下文
                MdcContext.clear();
                //
                MdcContext.flush();
            }
        }
    }

}
