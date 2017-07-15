package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.entity.AuthResponse;
import com.amocrm.amocrmclient.entity.account.current.ACData;
import com.amocrm.amocrmclient.iface.IAccountAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Component
public class AmoCrmAccountService {


    private static final Logger logger = LoggerFactory.getLogger(AmoCrmAccountService.class);

    AmoCrmAuthService authService;

    @Inject
    public AmoCrmAccountService(AmoCrmAuthService authService) {
        this.authService = authService;
    }

    public Response<ACData> data(OkHttpClient httpClient, Map<String, String> projectSettings) {

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

                IAccountAPI accountAPI = retrofit.create(IAccountAPI.class);

                return accountAPI.current().execute();
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Error reading account current", e);
        }
        return null;
    }

    public OkHttpClient getOkHttpClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(logging);

        CookieHandler cookieHandler = new CookieManager();
        JavaNetCookieJar jncj = new JavaNetCookieJar(cookieHandler);

        httpClientBuilder.cookieJar(jncj);
        httpClientBuilder.build();

        return httpClientBuilder.build();
    }
}
