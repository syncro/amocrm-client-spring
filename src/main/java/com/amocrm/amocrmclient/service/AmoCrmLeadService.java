package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.lead.LeadClient;
import com.amocrm.amocrmclient.lead.LeadClientBuilder;
import com.amocrm.amocrmclient.lead.entity.set.SLResponseData;
import com.amocrm.amocrmclient.lead.entity.list.LLResponseData;
import com.amocrm.amocrmclient.lead.entity.set.SLParam;
import com.amocrm.amocrmclient.service.configuration.AmoCrmClientConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Response;

@Component
public class AmoCrmLeadService extends AmoCrmService {

    private static final Logger logger = LoggerFactory.getLogger(AmoCrmLeadService.class);

    private LeadClient leadClient;

    @Inject
    public AmoCrmLeadService(AmoCrmClientConfig config) {
        this.leadClient = new LeadClientBuilder()
                .baseUrl(config.getBaseUrl())
                .login(config.getLogin())
                .passwordHash(config.getPasswordHash())
                .build();
    }

    public SLParam createLead(String name, int price) {

        return leadClient.createLead(name, price);
    }

    public Response<SLResponseData> setLead(SLParam setLead) {

        try {
            return leadClient.setLead(setLead);
        } catch (IOException e) {
            logger.error("Error setting lead", e);
        }

        return null;
    }

    public Response<SLResponseData> setLead(String name, int price) {

        try {
            return leadClient.setLead(name, price);
        } catch (IOException e) {
            logger.error("Error setting lead", e);
        }

        return null;
    }

    public Response<LLResponseData> list(String query, Long id, String responsibleUserId, String status, int limitRows, int limitOffset) {

        try {
            return leadClient.list(query, id, responsibleUserId, status, limitRows, limitOffset);
        } catch (IOException e) {
            logger.error("Error requesting lead list", e);
        }

        return null;
    }

    public Response<LLResponseData> list(String query) {

        return list(query, null, null, null, -1, -1);
    }

    public Response<LLResponseData> list() {

        return list(null, null, null, null, -1, -1);
    }

}
