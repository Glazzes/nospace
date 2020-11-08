package com.nospace.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "jwt.properties")
@Component
public class JwtProperties {
    private String prefix;
    private Long expirationInDays;
    private String secretKey;
}
