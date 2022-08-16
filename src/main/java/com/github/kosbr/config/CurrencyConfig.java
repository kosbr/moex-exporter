package com.github.kosbr.config;

/**
 * DTO object of config "app.currency.[key]" from application.properties
 */
public class CurrencyConfig {

    public final String code;
    public final String name;
    public final String tag;

    public CurrencyConfig(String code, String name, String tag) {
        this.code = code;
        this.name = name;
        this.tag = tag;
    }
}
