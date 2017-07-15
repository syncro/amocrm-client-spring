package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.entity.AuthResponse;
import com.amocrm.amocrmclient.entity.CustomField;
import com.amocrm.amocrmclient.entity.CustomFieldValue;
import com.amocrm.amocrmclient.entity.account.current.ACData;
import com.amocrm.amocrmclient.entity.account.CustomFieldSettings;
import com.amocrm.amocrmclient.entity.company.list.LCResponseData;
import com.amocrm.amocrmclient.entity.company.set.SCParam;
import com.amocrm.amocrmclient.entity.company.set.SCRequest;
import com.amocrm.amocrmclient.entity.company.set.SCRequestAdd;
import com.amocrm.amocrmclient.entity.company.set.SCRequestContacts;
import com.amocrm.amocrmclient.entity.company.set.SCResponseData;
import com.amocrm.amocrmclient.iface.ICompanyAPI;

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
public class AmoCrmCompanyService extends AmoCrmService {

    private static final Logger logger = LoggerFactory.getLogger(AmoCrmCompanyService.class);

    @Inject public AmoCrmCompanyService(AmoCrmAuthService authService, AmoCrmAccountService amoCrmAccountService) {
        super(authService, amoCrmAccountService);
    }

    public SCParam createCompany(String name) {

        SCParam setCompany = new SCParam();
        setCompany.request = new SCRequest();
        setCompany.request.contacts = new SCRequestContacts();
        setCompany.request.contacts.add = new ArrayList<>();
        SCRequestAdd setCompanyAdd = new SCRequestAdd();
        setCompanyAdd.name = name;
        setCompany.request.contacts.add.add(setCompanyAdd);

        return setCompany;
    }

    public SCParam setCompanyCustomFields(SCParam setCompany, Map<String, String> projectSettings,
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
                setCompany.request.contacts.add.get(0).linkedLeadsId = new ArrayList<>();
                setCompany.request.contacts.add.get(0).linkedLeadsId.add(linkedLeadId);
            }

            setCompany.request.contacts.add.get(0).customFields = new ArrayList<>();
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
                    setCompany.request.contacts.add.get(0).customFields.add(customField);
                } else {

                }


            }
            return setCompany;
        }
        return null;
    }

    public Response<SCResponseData> setCompany(SCParam setCompany, Map<String, String> projectSettings) {

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

                ICompanyAPI companyAPI = retrofit.create(ICompanyAPI.class);

                return companyAPI.setCompany(setCompany).execute();
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Error placing the lead", e);
        }
        return null;
    }

    public Response<LCResponseData> list(Map<String, String> projectSettings, String query, int limitRows, int limitOffset, Long id, String responsibleUserId) {

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

                ICompanyAPI companyAPI = retrofit.create(ICompanyAPI.class);

                if (id != null) {
                    return companyAPI.list(id).execute();
                } else if (responsibleUserId != null) {
                    return companyAPI.listByResponsibleUserId(responsibleUserId).execute();
                } else {
                    if (limitRows >= 0 && limitOffset >= 0 && query != null) {
                        return companyAPI.list(query, limitRows, limitOffset).execute();
                    } else if (query == null && limitRows >= 0 && limitOffset >= 0) {
                        return companyAPI.list(limitRows, limitOffset).execute();
                    } else {
                        return companyAPI.list().execute();
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

        return this.list(projectSettings, query, -1, -1, null, null);
    }

    public Response<LCResponseData> list(Map<String, String> projectSettings) {

        return this.list(projectSettings, null, -1, -1, null, null);
    }

}
