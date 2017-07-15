package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.entity.AuthResponse;
import com.amocrm.amocrmclient.entity.transaction.set.STParameter;
import com.amocrm.amocrmclient.entity.transaction.set.STAddTransaction;
import com.amocrm.amocrmclient.entity.transaction.set.STRequest;
import com.amocrm.amocrmclient.entity.transaction.set.STTransactions;
import com.amocrm.amocrmclient.entity.transaction.set.STResponseData;
import com.amocrm.amocrmclient.iface.ITransactionAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Component
public class AmoCrmTransactionService extends AmoCrmService {

    private static final Logger logger = LoggerFactory.getLogger(AmoCrmTransactionService.class);

    @Inject public AmoCrmTransactionService(AmoCrmAuthService authService, AmoCrmAccountService amoCrmAccountService) {
        super(authService, amoCrmAccountService);
    }

    public STParameter createTransaction(int price, long customerId, long date) {

        STParameter setTransaction = new STParameter();
        setTransaction.request = new STRequest();
        setTransaction.request.transactions = new STTransactions();
        setTransaction.request.transactions.add = new ArrayList<>();
        STAddTransaction setTransactionAdd = new STAddTransaction();
        setTransactionAdd.price = price;
        setTransactionAdd.customerId = customerId;
        setTransactionAdd.date = date;
        setTransaction.request.transactions.add.add(setTransactionAdd);

        return setTransaction;
    }

    public Response<STResponseData> setTransaction(STParameter setTransaction, Map<String, String> projectSettings) {

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

}
