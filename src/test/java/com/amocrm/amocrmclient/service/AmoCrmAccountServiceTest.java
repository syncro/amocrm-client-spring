package com.amocrm.amocrmclient.service;

import com.amocrm.amocrmclient.entity.account.AccountsDataResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:serviceContext-test.xml")
public class AmoCrmAccountServiceTest {

    @Autowired
    AmoCrmAccountService amoCrmAccountService;

    @Value("${amocrm.host}")
    private String amoCrmHost;

    @Value("${amocrm.user}")
    private String amoCrmUser;

    @Value("${amocrm.password}")
    private String amoCrmPassword;

    @Test
    public void testData() throws Exception {
        Map<String, String> projectSettings = new HashMap<>();
        projectSettings.put("amoCrmHost", amoCrmHost);
        projectSettings.put("amoCrmUser", amoCrmUser);
        projectSettings.put("amoCrmPassword", amoCrmPassword);
        OkHttpClient httpClient = amoCrmAccountService.getOkHttpClient();
        Response<AccountsDataResponse> response = amoCrmAccountService.data(httpClient, projectSettings);
        assertEquals(amoCrmHost, "https://" + response.body().response.account.name + ".amocrm.com/");
    }

}