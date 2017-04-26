package com.amocrm.amocrmclient.service;

import com.amocrm.amocrmclient.entity.customer.SetCustomer;
import com.amocrm.amocrmclient.entity.customer.SetCustomerResponse;

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


@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:serviceContext-test.xml")
public class AmoCrmCustomerServiceTest {

    @Autowired
    AmoCrmAccountService amoCrmAccountService;

    @Autowired
    AmoCrmCustomerService amoCrmCustomerService;

    @Value("${amocrm.host}")
    private String amoCrmHost;

    @Value("${amocrm.user}")
    private String amoCrmUser;

    @Value("${amocrm.password}")
    private String amoCrmPassword;

    @Test
    public void testSetAndDeleteCustomer() throws Exception {
        // TODO: Test update also
        Map<String, String> projectSettings = new HashMap<>();
        projectSettings.put("amoCrmHost", amoCrmHost);
        projectSettings.put("amoCrmUser", amoCrmUser);
        projectSettings.put("amoCrmPassword", amoCrmPassword);
        OkHttpClient httpClient = amoCrmAccountService.getOkHttpClient();
        SetCustomer setCustomer = amoCrmCustomerService.createCustomer("John Doe");
        Response<SetCustomerResponse> setCustomerResponse = amoCrmCustomerService.setCustomer(setCustomer, projectSettings);
        setCustomerResponse.isSuccessful();
/*
        SetCustomer deleteCustomer = new SetCustomer();
        setCustomer.request = new SetCustomerRequest();
        setCustomer.request.customers = new SetCustomerRequestCustomers();
        setCustomer.request.customers.delete = new ArrayList<>();
        setCustomer.request.customers.delete.add(setCustomerResponse.body().response.customers.add.customers.get(0).id);
        Response<SetCustomerResponse> deleteCustomerResponse = amoCrmCustomerService.setCustomer(deleteCustomer, projectSettings);
*/
        //assertEquals(amoCrmHost, "https://" + response.body().response.account.name + ".amocrm.com/");

    }

}