package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.customer.CustomerClient;
import com.amocrm.amocrmclient.customer.impl.CustomerClientBuilder;

import com.amocrm.amocrmclient.customer.entity.list.LCFilter;
import com.amocrm.amocrmclient.customer.entity.list.LCResponseData;
import com.amocrm.amocrmclient.customer.entity.set.SCParam;
import com.amocrm.amocrmclient.customer.entity.set.SCResponseData;

import com.amocrm.amocrmclient.service.configuration.AmoCrmClientConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Response;

@Component
public class AmoCrmCustomerService extends AmoCrmService {

    private static final Logger logger = LoggerFactory.getLogger(AmoCrmCustomerService.class);

    private CustomerClient customerClient;

    @Inject
    public AmoCrmCustomerService(AmoCrmClientConfig config) {
        this.customerClient = new CustomerClientBuilder()
                .baseUrl(config.getBaseUrl())
                .login(config.getLogin())
                .passwordHash(config.getPasswordHash())
                .build();
    }

    public SCParam createCustomer(String name) {

        return customerClient.createCustomer(name);
    }

    public SCParam setCustomFields(SCParam setCustomer, Map<String, String> fieldValues) {

        try {
            return customerClient.setCustomFields(setCustomer, fieldValues);
        } catch (IOException e) {
            logger.error("Error setting custom fields for customer", e);
        }

        return null;
    }

    public Response<SCResponseData> setCustomer(SCParam setCustomer) {

        try {
            return customerClient.setCustomer(setCustomer);
        } catch (IOException e) {
            logger.error("Error setting customer", e);
        }

        return null;
    }


    public Response<SCResponseData> setCustomer(String name) {

        try {
            return customerClient.setCustomer(name);
        } catch (IOException e) {
            logger.error("Error setting customer", e);
        }

        return null;
    }

    public Response<LCResponseData> list(LCFilter filter, int limitRows, int limitOffset) {

        try {
            return customerClient.list(filter, limitRows, limitOffset);
        } catch (IOException e) {
            logger.error("Error requesting customer list", e);
        }

        return null;
    }

    public Response<LCResponseData> list(LCFilter filter) {

        return list(filter, -1, -1);
    }

    public Response<LCResponseData> list() {

        return list(null, -1, -1);
    }

}
