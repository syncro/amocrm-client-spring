package com.amocrm.amocrmclient.service.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "amocrm")
public class AmoCrmClientConfig {

    private String baseUrl;

    private String login;

    private String passwordHash;

}
