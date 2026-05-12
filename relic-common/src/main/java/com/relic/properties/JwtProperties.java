package com.relic.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "relic.jwt")
@Data
public class JwtProperties {

    private String adminSecretKey;
    private long adminTtl;
    private String adminTokenName;

    private String knowledgeSecretKey;
    private long knowledgeTtl;
    private String knowledgeTokenName;

    private String museumSecretKey;
    private long museumTtl;
    private String museumTokenName;

    private String userSecretKey;
    private long userTtl;
    private String userTokenName;

}
