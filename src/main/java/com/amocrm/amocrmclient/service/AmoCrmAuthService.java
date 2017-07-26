package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.auth.AuthClient;
import com.amocrm.amocrmclient.auth.AuthClientBuilder;
import com.amocrm.amocrmclient.entity.AuthResponse;
import com.amocrm.amocrmclient.service.configuration.AmoCrmClientConfig;

import org.springframework.stereotype.Service;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Service
public class AmoCrmAuthService {

    private AuthClient authClient;

    @Inject
    public AmoCrmAuthService(AmoCrmClientConfig config) {
        // for other services retrofit initialized shared inside builders
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(config.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        this.authClient = new AuthClientBuilder()
                .login(config.getLogin())
                .passwordHash(config.getPasswordHash())
                .retrofit(retrofit)
                .build();
    }

    public Call<AuthResponse> auth() {

        return authClient.auth();
    }

}
