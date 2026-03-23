package com.example.StarterPack.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "app")
@Setter
@Getter
public class ApplicationProperties {
    private List<String> allowedOrigins;
    private String applicationName;
    private String baseUrl;
    private String loginPageUrl;
}
