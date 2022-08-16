package com.github.kosbr;

import com.github.kosbr.client.MoexClient;
import com.github.kosbr.config.CurrencyConfig;
import com.github.kosbr.config.CurrencyConfigExtractor;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.quarkus.scheduler.Scheduled;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

/**
 * This class periodically takes data from moscow exchange and puts it to metrics according to config
 */
@ApplicationScoped
public class MoexDataGrabber {

    private static final Logger LOG = Logger.getLogger(MoexDataGrabber.class);

    private static final String LAST_PRICE_COLUMN = "LAST";
    private static final String CODE_PREFIX = "CETS:";
    private static final String ISS_ONLY = "marketdata";

    @Inject
    private MeterRegistry registry;

    @Inject
    @RestClient
    private MoexClient moexClient;

    private Set<CurrencyConfig> currencyConfigs = CurrencyConfigExtractor.extractConfig();

    private Map<String, Double> lastPrices = new HashMap<>();

    @ConfigProperty(name = "app.metric.name")
    private String metricName;

    @ConfigProperty(name = "app.metric.tag.name")
    private String tagName;

    @PostConstruct
    void init() {
        currencyConfigs.forEach(currencyConfig -> {
            registry.gauge(metricName, Collections.singleton(Tag.of(tagName, currencyConfig.tag)), lastPrices,
                    map -> map.get(currencyConfig.code));
        });
    }

    @Scheduled(every="{app.grab.period}")
    void updateMetrics() {
        currencyConfigs.forEach(currencyConfig -> {
            try {
                var response = moexClient.getData(CODE_PREFIX + currencyConfig.code, ISS_ONLY);
                var idxOfLast = response.marketdata.columns.indexOf(LAST_PRICE_COLUMN);
                var price = response.marketdata.data.get(0).get(idxOfLast);
                var doublePrice = Double.parseDouble(price);
                lastPrices.put(currencyConfig.code, doublePrice);
                LOG.info(String.format("Currency value received: %s = %f", currencyConfig.code, doublePrice));
            } catch (Throwable e) {
                LOG.error(e.getMessage(), e);
                lastPrices.put(currencyConfig.code, null);
            }
        });
    }

}
