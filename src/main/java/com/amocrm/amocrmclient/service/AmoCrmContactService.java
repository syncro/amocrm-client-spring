package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.entity.AuthResponse;
import com.amocrm.amocrmclient.entity.CustomField;
import com.amocrm.amocrmclient.entity.CustomFieldValue;
import com.amocrm.amocrmclient.entity.account.current.ACData;
import com.amocrm.amocrmclient.entity.account.CustomFieldSettings;
import com.amocrm.amocrmclient.entity.contact.links.CLResponseData;
import com.amocrm.amocrmclient.entity.contact.list.LCResponseData;
import com.amocrm.amocrmclient.entity.contact.set.SCResponseData;
import com.amocrm.amocrmclient.entity.contact.set.SCParam;
import com.amocrm.amocrmclient.entity.contact.set.SCAdd;
import com.amocrm.amocrmclient.entity.contact.set.SCRequest;
import com.amocrm.amocrmclient.entity.contact.set.SCRequestContacts;
import com.amocrm.amocrmclient.iface.IContactAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Component
public class AmoCrmContactService extends AmoCrmService {

    private static final Logger logger = LoggerFactory.getLogger(AmoCrmContactService.class);

    @Inject public AmoCrmContactService(AmoCrmAuthService authService, AmoCrmAccountService amoCrmAccountService) {
        super(authService, amoCrmAccountService);
    }

    public SCParam createContact(String name) {

        SCParam setContact = new SCParam();
        setContact.request = new SCRequest();
        setContact.request.contacts = new SCRequestContacts();
        setContact.request.contacts.add = new ArrayList<>();
        SCAdd setContactAdd = new SCAdd();
        setContactAdd.name = name;
        setContact.request.contacts.add.add(setContactAdd);

        return setContact;
    }

    public SCParam setContactCustomFields(SCParam setContact, Map<String, String> projectSettings,
                                          Map<String, String> fieldValues, Long linkedLeadId) {

        OkHttpClient httpClient = getOkHttpClient();

        Response<ACData> accountsDataResponse = amoCrmAccountService.data(httpClient, projectSettings);

        if (accountsDataResponse.isSuccessful()) {

            List<CustomFieldSettings> customFields =
                    accountsDataResponse.body().response.account.customFields.contacts;

            Map<String, CustomFieldSettings> customFieldsMap = new HashMap<>();

            for (CustomFieldSettings customField : customFields) {
                customFieldsMap.put(customField.name, customField);
            }

            if (linkedLeadId != null) {
                setContact.request.contacts.add.get(0).linkedLeadsId = new ArrayList<>();
                setContact.request.contacts.add.get(0).linkedLeadsId.add(linkedLeadId);
            }

            setContact.request.contacts.add.get(0).customFields = new ArrayList<>();
            for (String fieldName : fieldValues.keySet()) {
                CustomFieldSettings customFieldSettings = customFieldsMap.get(fieldName);
                if ("Y".equals(customFieldSettings.multiple)) {
                    CustomField customField = new CustomField();
                    customField.id = customFieldsMap.get(fieldName).id;
                    customField.values = new ArrayList<>();
                    CustomFieldValue fieldValue = new CustomFieldValue();
                    fieldValue.value = fieldValues.get(fieldName);
                    customField.values.add(fieldValue);
                    if ("Phone".equals(fieldName)) {
                        fieldValue.enumer = "MOB";
                    } else if ("Email".equals(fieldName)) {
                        fieldValue.enumer = "WORK";
                    }
                    setContact.request.contacts.add.get(0).customFields.add(customField);
                } else {

                }


            }
            return setContact;
        }
        return null;
    }

    public Response<SCResponseData> setContact(SCParam setContact, Map<String, String> projectSettings) {

        OkHttpClient httpClient = getOkHttpClient();

        Call<AuthResponse> authResponse = authService.auth(httpClient, projectSettings.get("amoCrmHost"),
                projectSettings.get("amoCrmUser"),  projectSettings.get("amoCrmPassword"));

        Response response = null;
        try {
            response = authResponse.execute();
            if (response.isSuccessful()) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(projectSettings.get("amoCrmHost"))
                        .client(httpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                IContactAPI contactAPI = retrofit.create(IContactAPI.class);

                return contactAPI.setContact(setContact).execute();
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Error placing the lead", e);
        }
        return null;
    }


    public Response<LCResponseData> list(Map<String, String> projectSettings, String query, int limitRows, int limitOffset, Long id, String responsibleUserId, String type) {

        OkHttpClient httpClient = getOkHttpClient();

        Call<AuthResponse> authResponse = authService.auth(httpClient, projectSettings.get("amoCrmHost"),
                projectSettings.get("amoCrmUser"),  projectSettings.get("amoCrmPassword"));

        Response response = null;
        try {
            response = authResponse.execute();
            if (response.isSuccessful()) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(projectSettings.get("amoCrmHost"))
                        .client(httpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                IContactAPI contactAPI = retrofit.create(IContactAPI.class);

                if (type != null) {
                    return contactAPI.listByType(type).execute();
                } else if (id != null) {
                    return contactAPI.list(id).execute();
                } else if (responsibleUserId != null) {
                    return contactAPI.listByResponsibleUserId(responsibleUserId).execute();
                } else {
                    if (limitRows >= 0 && limitOffset >= 0 && query != null) {
                        return contactAPI.list(query, limitRows, limitOffset).execute();
                    } else if (query == null && limitRows >= 0 && limitOffset >= 0) {
                        return contactAPI.list(limitRows, limitOffset).execute();
                    } else {
                        return contactAPI.list().execute();
                    }
                }

            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Error fetching contact list", e);
        }
        return null;
    }

    public Response<LCResponseData> list(Map<String, String> projectSettings, String query) {

        return this.list(projectSettings, query, -1, -1, null, null, null);
    }

    public Response<LCResponseData> list(Map<String, String> projectSettings) {

        return this.list(projectSettings, null, -1, -1, null, null, null);
    }

    public Response<CLResponseData> links(Map<String, String> projectSettings) {

        OkHttpClient httpClient = getOkHttpClient();

        Call<AuthResponse> authResponse = authService.auth(httpClient, projectSettings.get("amoCrmHost"),
                projectSettings.get("amoCrmUser"),  projectSettings.get("amoCrmPassword"));

        Response response = null;
        try {
            response = authResponse.execute();
            if (response.isSuccessful()) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(projectSettings.get("amoCrmHost"))
                        .client(httpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                IContactAPI contactAPI = retrofit.create(IContactAPI.class);

                return contactAPI.links().execute();
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Error fetching contact links list", e);
        }
        return null;
    }

}
