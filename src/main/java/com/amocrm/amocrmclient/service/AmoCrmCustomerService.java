package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.entity.AuthResponse;
import com.amocrm.amocrmclient.entity.CustomField;
import com.amocrm.amocrmclient.entity.CustomFieldValue;
import com.amocrm.amocrmclient.entity.account.current.ACData;
import com.amocrm.amocrmclient.entity.account.CustomFieldSettings;

import com.amocrm.amocrmclient.entity.customer.list.LCFilter;
import com.amocrm.amocrmclient.entity.customer.list.LCResponseData;
import com.amocrm.amocrmclient.entity.customer.set.SCParam;
import com.amocrm.amocrmclient.entity.customer.set.SCAdd;
import com.amocrm.amocrmclient.entity.customer.set.SCRequest;
import com.amocrm.amocrmclient.entity.customer.set.SCRequestCustomers;
import com.amocrm.amocrmclient.entity.customer.set.SCResponseData;

import com.amocrm.amocrmclient.iface.ICustomerAPI;

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
public class AmoCrmCustomerService extends AmoCrmService {

    private static final Logger logger = LoggerFactory.getLogger(AmoCrmCustomerService.class);

    @Inject public AmoCrmCustomerService(AmoCrmAuthService authService, AmoCrmAccountService amoCrmAccountService) {
        super(authService, amoCrmAccountService);
    }

    public SCParam createCustomer(String name) {

        SCParam setCustomer = new SCParam();
        setCustomer.request = new SCRequest();
        setCustomer.request.customers = new SCRequestCustomers();
        setCustomer.request.customers.add = new ArrayList<>();
        SCAdd setCustomerAdd = new SCAdd();
        setCustomerAdd.name = name;
        setCustomer.request.customers.add.add(setCustomerAdd);

        return setCustomer;
    }

    public SCParam setCustomFields(SCParam setCustomer, Map<String, String> projectSettings, Map<String, String> fieldValues) {

        OkHttpClient httpClient = getOkHttpClient();

        Response<ACData> accountsDataResponse = amoCrmAccountService.data(httpClient, projectSettings);

        if (accountsDataResponse.isSuccessful()) {

            List<CustomFieldSettings> customFields =
                    accountsDataResponse.body().response.account.customFields.contacts;

            Map<String, CustomFieldSettings> customFieldsMap = new HashMap<>();

            for (CustomFieldSettings customField : customFields) {
                customFieldsMap.put(customField.name, customField);
            }

            setCustomer.request.customers.add.get(0).customFields = new ArrayList<>();
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
                    setCustomer.request.customers.add.get(0).customFields.add(customField);
                } else {

                }


            }
            return setCustomer;
        }
        return null;
    }

    public Response<SCResponseData> setCustomer(SCParam setCustomer, Map<String, String> projectSettings) {

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

                ICustomerAPI customerAPI = retrofit.create(ICustomerAPI.class);

                return customerAPI.setCustomer(setCustomer).execute();
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Error placing the lead", e);
        }
        return null;
    }

    public Response<LCResponseData> list(Map<String, String> projectSettings, LCFilter filter, int limitRows, int limitOffset) {

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

                ICustomerAPI customerAPI = retrofit.create(ICustomerAPI.class);

                if (filter != null) {
                    if (limitRows >= 0 && limitOffset >= 0) {
                        return customerAPI.list(filter, limitRows, limitOffset).execute();
                    } else if (limitRows >= 0) {
                        return customerAPI.list(limitRows).execute();
                    }

                    return customerAPI.list(filter).execute();

                }
                if (limitRows >= 0 && limitOffset >= 0) {
                    return customerAPI.list(limitRows, limitOffset).execute();
                } else if (limitRows >= 0) {
                    return customerAPI.list(limitRows).execute();
                }

                return customerAPI.list().execute();

            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Error placing the lead", e);
        }
        return null;
    }

    public Response<LCResponseData> list(Map<String, String> projectSettings, LCFilter filter) {

        return this.list(projectSettings, filter, -1, -1);
    }

    public Response<LCResponseData> list(Map<String, String> projectSettings) {

        return this.list(projectSettings, null, -1, -1);
    }

}
