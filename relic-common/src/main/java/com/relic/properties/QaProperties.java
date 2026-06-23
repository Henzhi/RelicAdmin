package com.relic.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "relic.qa")
public class QaProperties {
    private String baseUrl = "http://127.0.0.1:8000";
}
