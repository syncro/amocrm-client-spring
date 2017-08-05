package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.service.configuration.AmoCrmClientConfig;
import com.amocrm.amocrmclient.transaction.TransactionClient;
import com.amocrm.amocrmclient.transaction.impl.TransactionClientBuilder;
import com.amocrm.amocrmclient.transaction.entity.set.STParameter;
import com.amocrm.amocrmclient.transaction.entity.set.STResponseData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Response;

@Component
public class AmoCrmTransactionService extends AmoCrmService {

    private static final Logger logger = LoggerFactory.getLogger(AmoCrmTransactionService.class);

    private TransactionClient transactionClient;

    @Inject
    public AmoCrmTransactionService(AmoCrmClientConfig config) {
        this.transactionClient = new TransactionClientBuilder()
                .baseUrl(config.getBaseUrl())
                .login(config.getLogin())
                .passwordHash(config.getPasswordHash())
                .build();
    }

    public STParameter createTransaction(int price, long customerId, long date) {

        return transactionClient.createTransaction(price, customerId, date);
    }

    public Response<STResponseData> setTransaction(STParameter setTransaction) {

        try {
            return transactionClient.setTransaction(setTransaction);
        } catch (IOException e) {
            logger.error("Error setting transaction", e);
        }

        return null;
    }

    public Response<STResponseData> setTransaction(int price, long customerId, long date) {

        try {
            return transactionClient.setTransaction(price, customerId, date);
        } catch (IOException e) {
            logger.error("Error setting transaction", e);
        }

        return null;
    }
}
