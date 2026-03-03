package com.khangmoihocit.learn.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Getter
    @Value("${jwt.secret}")
    private String secretKey;

    @Getter
    @Value("${jwt.expiration}")
    private long expirationTime;

    @Getter
    @Value("${jwt.expirationRefreshToken}")
    private long expirationTimeRefreshToken;

    @Getter
    @Value("${jwt.defaultExpiration}")
    private long defaultExpiration;

    @Getter
    @Value("${jwt.issuer}")
    private String issuer;

}
