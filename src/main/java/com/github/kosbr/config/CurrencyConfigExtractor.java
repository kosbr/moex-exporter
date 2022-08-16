package com.github.kosbr.config;

import java.util.Set;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 * Extracts configs from application.properties about needed currencies.
 */
public class CurrencyConfigExtractor {

    private static final String CURRENCY_CONFIG_PREFIX = "app.currency";
    private static final int CURRENCY_CONFIG_PREFIX_LEVEL = CURRENCY_CONFIG_PREFIX.split("\\.").length;

    private static final String CODE_PROPERTY = "code";
    private static final String NAME_PROPERTY = "name";
    private static final String TAG_PROPERTY = "tag";


    public static Set<CurrencyConfig> extractConfig() {
        Set<String> keys = getKeys();
        return keys.stream().map(key -> new CurrencyConfig(
                getValue(key, CODE_PROPERTY),
                getValue(key, NAME_PROPERTY),
                getValue(key, TAG_PROPERTY)
        )).collect(Collectors.toSet());
    }

    private static Set<String> getKeys() {
        Spliterator<String> configsSpliterator = ConfigProvider.getConfig().getPropertyNames().spliterator();
        return StreamSupport.stream(configsSpliterator, false)
                .filter(it -> it.startsWith(CURRENCY_CONFIG_PREFIX))
                .map(it -> it.split("\\.")[CURRENCY_CONFIG_PREFIX_LEVEL])
                .collect(Collectors.toSet());
    }

    private static String getValue(String key, String property) {
        return ConfigProvider.getConfig().getValue(CURRENCY_CONFIG_PREFIX + "." + key + "." + property,
                String.class);
    }

}
