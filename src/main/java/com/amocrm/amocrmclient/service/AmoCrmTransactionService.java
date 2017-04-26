package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.entity.AuthResponse;
import com.amocrm.amocrmclient.entity.CustomField;
import com.amocrm.amocrmclient.entity.CustomFieldValue;
import com.amocrm.amocrmclient.entity.account.AccountsDataResponse;
import com.amocrm.amocrmclient.entity.account.CustomFieldSettings;
import com.amocrm.amocrmclient.entity.contact.AddContactResponse;
import com.amocrm.amocrmclient.entity.contact.SetContact;
import com.amocrm.amocrmclient.entity.customer.SetCustomer;
import com.amocrm.amocrmclient.entity.customer.SetCustomerAdd;
import com.amocrm.amocrmclient.entity.customer.SetCustomerRequest;
import com.amocrm.amocrmclient.entity.customer.SetCustomerRequestCustomers;
import com.amocrm.amocrmclient.entity.transaction.SetTransaction;
import com.amocrm.amocrmclient.entity.transaction.SetTransactionAdd;
import com.amocrm.amocrmclient.entity.transaction.SetTransactionAddTransaction;
import com.amocrm.amocrmclient.entity.transaction.SetTransactionRequest;
import com.amocrm.amocrmclient.entity.transaction.SetTransactionRequestTransactions;
import com.amocrm.amocrmclient.entity.transaction.SetTransactionResponse;
import com.amocrm.amocrmclient.iface.IContactAPI;
import com.amocrm.amocrmclient.iface.ITransactionAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    AmoCrmAccountService amoCrmAccountService;

    @Inject public AmoCrmTransactionService(AmoCrmAuthService authService, AmoCrmAccountService amoCrmAccountService) {
        this.authService = authService;
        this.amoCrmAccountService = amoCrmAccountService;
    }


    public SetTransaction createTransaction(int price) {

        SetTransaction setTransaction = new SetTransaction();
        setTransaction.request = new SetTransactionRequest();
        setTransaction.request.transactions = new SetTransactionRequestTransactions();
        setTransaction.request.transactions.add = new ArrayList<>();
        SetTransactionAddTransaction setTransactionAdd = new SetTransactionAddTransaction();
        setTransactionAdd.price = price;
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
