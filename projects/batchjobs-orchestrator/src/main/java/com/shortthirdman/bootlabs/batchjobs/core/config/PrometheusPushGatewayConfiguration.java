package com.shortthirdman.bootlabs.batchjobs.core.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "prometheus.pushgateway")
public class PrometheusPushGatewayConfiguration {

    @NotNull
    @NotEmpty
    private String url;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.profiles.active:default}")
    private String environment;

    @Value("${HOSTNAME:unknown-instance}")
    private String instanceId;
}
