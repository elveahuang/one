package cc.wdev.platform.commons.core.exchange;

import org.springframework.web.client.RestClient;

/**
 * @author elvea
 */
@FunctionalInterface
public interface HttpExchangeCustomizer {

    RestClient.Builder apply(RestClient restClient);

}
