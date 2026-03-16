package com.jaycodesx.mortgage.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.reactive.CorsWebFilter;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    @Test
    void corsWebFilterCreatesFilterFromConfiguredOrigins() {
        CorsConfig corsConfig = new CorsConfig();

        CorsWebFilter filter = corsConfig.corsWebFilter("https://a.jaycodesx.dev, https://b.jaycodesx.dev");

        assertThat(filter).isNotNull();
    }
}
