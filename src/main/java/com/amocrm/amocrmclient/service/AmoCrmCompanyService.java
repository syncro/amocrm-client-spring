package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.company.CompanyClient;
import com.amocrm.amocrmclient.company.impl.CompanyClientBuilder;
import com.amocrm.amocrmclient.company.entity.list.LCResponseData;
import com.amocrm.amocrmclient.company.entity.set.SCParam;
import com.amocrm.amocrmclient.company.entity.set.SCResponseData;
import com.amocrm.amocrmclient.service.configuration.AmoCrmClientConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Response;

@Component
public class AmoCrmCompanyService extends AmoCrmService {

    private static final Logger logger = LoggerFactory.getLogger(AmoCrmCompanyService.class);

    private CompanyClient companyClient;

    @Inject
    public AmoCrmCompanyService(AmoCrmClientConfig config) {
        this.companyClient = new CompanyClientBuilder()
                .baseUrl(config.getBaseUrl())
                .login(config.getLogin())
                .passwordHash(config.getPasswordHash())
                .build();
    }

    public SCParam createCompany(String name) {

        return companyClient.createCompany(name);
    }

    public SCParam setCompanyCustomFields(SCParam setCompany, Map<String, String> fieldValues, Long linkedLeadId) {

        try {
            return companyClient.setCompanyCustomFields(setCompany, fieldValues, linkedLeadId);
        } catch (IOException e) {
            logger.error("Error setting custom fields for company", e);
        }

        return null;
    }

    public Response<SCResponseData> setCompany(SCParam setCompany) {

        try {
            return companyClient.setCompany(setCompany);
        } catch (IOException e) {
            logger.error("Error setting company", e);
        }

        return null;
    }

    public Response<SCResponseData> setCompany(String name) {

        try {
            return companyClient.setCompany(name);
        } catch (IOException e) {
            logger.error("Error setting company", e);
        }

        return null;
    }

    public Response<LCResponseData> list(String query, int limitRows, int limitOffset, Long id, String responsibleUserId) {

        try {
            return companyClient.list(query, limitRows, limitOffset, id, responsibleUserId);
        } catch (IOException e) {
            logger.error("Error requesting company list", e);
        }

        return null;
    }

    public Response<LCResponseData> list(String query) {

        return list(query, -1, -1, null, null);
    }

    public Response<LCResponseData> list() {

        return list(null, -1, -1, null, null);
    }

}
