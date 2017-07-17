package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.entity.lead.set.SLAdd;
import com.amocrm.amocrmclient.entity.lead.set.SLResponseData;
import com.amocrm.amocrmclient.entity.lead.set.SLLeads;
import com.amocrm.amocrmclient.entity.AuthResponse;
import com.amocrm.amocrmclient.entity.lead.list.LLResponseData;
import com.amocrm.amocrmclient.entity.lead.set.SLParam;
import com.amocrm.amocrmclient.entity.lead.set.SLRequest;
import com.amocrm.amocrmclient.iface.ILeadAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;

@Component
public class AmoCrmLeadService extends AmoCrmService {

    private static final Logger logger = LoggerFactory.getLogger(AmoCrmLeadService.class);

    @Inject public AmoCrmLeadService(AmoCrmAuthService authService, AmoCrmAccountService amoCrmAccountService) {
        super(authService, amoCrmAccountService);
    }

    public SLParam createLead(String name, int price) {

        SLParam setLead = new SLParam();
        SLAdd addLead = new SLAdd();
        addLead.name = name;
        addLead.price = new BigDecimal(price);
        setLead.request = new SLRequest();
        setLead.request.leads = new SLLeads();
        setLead.request.leads.add = new ArrayList<>();
        setLead.request.leads.add.add(addLead);

        return setLead;
    }

    public Response<SLResponseData> setLead(@Body SLParam setLead, Map<String, String> projectSettings) {

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

                ILeadAPI leadAPI = retrofit.create(ILeadAPI.class);

                return leadAPI.setLead(setLead).execute();

            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Error placing the lead", e);
        }
        return null;
    }

    public Response<LLResponseData> list(Map<String, String> projectSettings, String query, Long id, String responsibleUserId, String status, int limitRows, int limitOffset) {

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

                ILeadAPI leadAPI = retrofit.create(ILeadAPI.class);

                if (id != null) {

                    return leadAPI.list(id).execute();

                } else if (responsibleUserId != null) {

                    return leadAPI.listByResponsibleUserId(responsibleUserId).execute();

                } else if (query != null) {

                    if (limitRows >= 0 && limitOffset >= 0) {
                        return leadAPI.list(query, limitRows, limitOffset).execute();
                    } else if (limitRows >= 0) {
                        return leadAPI.list(query, limitRows).execute();
                    }

                    return leadAPI.list(query).execute();

                } else if (responsibleUserId != null) {

                    if (limitRows >= 0 && limitOffset >= 0) {
                        return leadAPI.listByResponsibleUserId(responsibleUserId, limitRows, limitOffset).execute();
                    } else if (limitRows >= 0) {
                        return leadAPI.listByResponsibleUserId(responsibleUserId, limitRows).execute();
                    }

                    return leadAPI.listByResponsibleUserId(responsibleUserId).execute();

                } else if (status != null) {

                    if (limitRows >= 0 && limitOffset >= 0) {
                        return leadAPI.listByStatusId(status, limitRows, limitOffset).execute();
                    } else if (limitRows >= 0) {
                        return leadAPI.listByStatusId(status, limitRows).execute();
                    }

                    return leadAPI.listByStatusId(status).execute();

                } else { //
                    return leadAPI.list().execute();
                }

            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Error fetching contact list", e);
        }

        return null;
    }

    public Response<LLResponseData> list(Map<String, String> projectSettings, String query) {

        return this.list(projectSettings, query, null, null, null, -1, -1);

    }

    public Response<LLResponseData> list(Map<String, String> projectSettings) {

        return this.list(projectSettings, null, null, null, null, -1, -1);

    }

}
