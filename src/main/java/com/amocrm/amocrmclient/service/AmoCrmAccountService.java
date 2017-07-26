package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.account.AccountClient;
import com.amocrm.amocrmclient.account.AccountClientBuilder;
import com.amocrm.amocrmclient.account.entity.current.ACData;
import com.amocrm.amocrmclient.service.configuration.AmoCrmClientConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Response;


@Service
public class AmoCrmAccountService {


    private static final Logger logger = LoggerFactory.getLogger(AmoCrmAccountService.class);

    private AccountClient accountClient;

    @Inject
    public AmoCrmAccountService(AmoCrmClientConfig config) {
        this.accountClient = new AccountClientBuilder()
                .baseUrl(config.getBaseUrl())
                .login(config.getLogin())
                .passwordHash(config.getPasswordHash())
                .build();
    }

    public Response<ACData> data() {
        try {
            return accountClient.data();
        } catch (IOException e) {
            logger.error("Error reading auth account data", e);
        }
        return null;
    }
}
