package com.amocrm.amocrmclient.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.CookieHandler;
import java.net.CookieManager;

import javax.inject.Inject;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

@Component
public class AmoCrmService {

    private static final Logger logger = LoggerFactory.getLogger(AmoCrmService.class);

    AmoCrmAuthService authService;

    AmoCrmAccountService amoCrmAccountService;

    @Inject
    public AmoCrmService(AmoCrmAuthService authService, AmoCrmAccountService amoCrmAccountService) {
        this.authService = authService;
        this.amoCrmAccountService = amoCrmAccountService;
    }

    public OkHttpClient getOkHttpClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(logging);

        CookieHandler cookieHandler = new CookieManager();
        JavaNetCookieJar jncj = new JavaNetCookieJar(cookieHandler);

        httpClientBuilder.cookieJar(jncj);
        httpClientBuilder.build();

        return httpClientBuilder.build();
    }
}
