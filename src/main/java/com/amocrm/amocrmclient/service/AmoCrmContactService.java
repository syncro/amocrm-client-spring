package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.contact.ContactClient;
import com.amocrm.amocrmclient.contact.impl.ContactClientBuilder;
import com.amocrm.amocrmclient.contact.entity.links.CLResponseData;
import com.amocrm.amocrmclient.contact.entity.list.LCResponseData;
import com.amocrm.amocrmclient.contact.entity.set.SCResponseData;
import com.amocrm.amocrmclient.contact.entity.set.SCParam;
import com.amocrm.amocrmclient.service.configuration.AmoCrmClientConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Response;

@Component
public class AmoCrmContactService extends AmoCrmService {

    private static final Logger logger = LoggerFactory.getLogger(AmoCrmContactService.class);

    private ContactClient contactClient;

    @Inject
    public AmoCrmContactService(AmoCrmClientConfig config) {
        this.contactClient = new ContactClientBuilder()
                .baseUrl(config.getBaseUrl())
                .login(config.getLogin())
                .passwordHash(config.getPasswordHash())
                .build();
    }

    public SCParam createContact(String name) {

        return contactClient.createContact(name);
    }

    public SCParam setContactCustomFields(SCParam setContact, Map<String, String> fieldValues, Long linkedLeadId) {

        try {
            return contactClient.setContactCustomFields(setContact, fieldValues, linkedLeadId);
        } catch (IOException e) {
            logger.error("Error setting custom fields for contact", e);
        }

        return null;
    }

    public Response<SCResponseData> setContact(SCParam setContact) {

        try {
            return contactClient.setContact(setContact);
        } catch (IOException e) {
            logger.error("Error setting company", e);
        }

        return null;
    }

    public Response<SCResponseData> setContact(String name) {

        try {
            return contactClient.setContact(name);
        } catch (IOException e) {
            logger.error("Error setting company", e);
        }

        return null;
    }

    public Response<LCResponseData> list(String query, int limitRows, int limitOffset, Long id, String responsibleUserId, String type) {

        try {
            return contactClient.list(query, limitRows, limitOffset, id, responsibleUserId, type);
        } catch (IOException e) {
            logger.error("Error requesting contact list", e);
        }

        return null;
    }

    public Response<LCResponseData> list(String query) {

        return list(query, -1, -1, null, null, null);
    }

    public Response<LCResponseData> list() {

        return list(null, -1, -1, null, null, null);
    }

    public Response<CLResponseData> links() {

        try {
            return contactClient.links();
        } catch (IOException e) {
            logger.error("Error requesting contact links", e);
        }

        return null;
    }

}
