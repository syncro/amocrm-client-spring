package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.entity.AuthResponse;
import com.amocrm.amocrmclient.entity.transaction.SetTransaction;
import com.amocrm.amocrmclient.entity.transaction.SetTransactionAddTransaction;
import com.amocrm.amocrmclient.entity.transaction.SetTransactionRequest;
import com.amocrm.amocrmclient.entity.transaction.SetTransactionRequestTransactions;
import com.amocrm.amocrmclient.entity.transaction.SetTransactionResponse;
import com.amocrm.amocrmclient.iface.ITransactionAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
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
public class AmoCrmTransactionService {

    private static final Logger logger = LoggerFactory.getLogger(AmoCrmTransactionService.class);

    AmoCrmAuthService authService;

    AmoCrmContactService amoCrmAccountService;

    @Inject public AmoCrmTransactionService(AmoCrmAuthService authService, AmoCrmContactService amoCrmAccountService) {
        this.authService = authService;
        this.amoCrmAccountService = amoCrmAccountService;
    }


    public SetTransaction createTransaction(int price, long customerId, long date) {

        SetTransaction setTransaction = new SetTransaction();
        setTransaction.request = new SetTransactionRequest();
        setTransaction.request.transactions = new SetTransactionRequestTransactions();
        setTransaction.request.transactions.add = new ArrayList<>();
        SetTransactionAddTransaction setTransactionAdd = new SetTransactionAddTransaction();
        setTransactionAdd.price = price;
        setTransactionAdd.customerId = customerId;
        setTransactionAdd.date = date;
        setTransaction.request.transactions.add.add(setTransactionAdd);

        return setTransaction;
    }

    public Response<SetTransactionResponse> setTransaction(SetTransaction setTransaction, Map<String, String> projectSettings) {

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

                ITransactionAPI transactionAPI = retrofit.create(ITransactionAPI.class);

                return transactionAPI.setTransaction(setTransaction).execute();
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Error placing transaction", e);
        }
        return null;
    }

    private OkHttpClient getOkHttpClient() {

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
