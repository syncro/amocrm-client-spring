package com.amocrm.amocrmclient.service;

import com.amocrm.amocrmclient.entity.customer.ListCustomersResponse;
import com.amocrm.amocrmclient.entity.customer.ResponseCustomersCustomer;
import com.amocrm.amocrmclient.entity.customer.SetCustomer;
import com.amocrm.amocrmclient.entity.customer.SetCustomerRequest;
import com.amocrm.amocrmclient.entity.customer.SetCustomerRequestCustomers;
import com.amocrm.amocrmclient.entity.customer.SetCustomerResponse;
import com.amocrm.amocrmclient.entity.customer.SetCustomerResponseCustomersSectionsDeleteCustomer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:serviceContext-test.xml")
public class AmoCrmCustomerServiceTest {

    @Autowired
    AmoCrmContactService amoCrmAccountService;

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
        assertEquals(setCustomerResponse.body().response.customers.add.customers.size(), 1);

        ResponseCustomersCustomer customer = setCustomerResponse.body().response.customers.add.customers.get(0);

        SetCustomer deleteCustomer = new SetCustomer();
        deleteCustomer.request = new SetCustomerRequest();
        deleteCustomer.request.customers = new SetCustomerRequestCustomers();
        deleteCustomer.request.customers.add = new ArrayList<>();
        deleteCustomer.request.customers.delete = new ArrayList<>();
        deleteCustomer.request.customers.delete.add(customer.id);
        Response<SetCustomerResponse> deleteCustomerResponse = amoCrmCustomerService.setCustomer(deleteCustomer, projectSettings);

        SetCustomerResponseCustomersSectionsDeleteCustomer deletedCustomer = deleteCustomerResponse.body().response.customers.delete.customers.get(String.valueOf(customer.id));
        assertEquals(deletedCustomer.id, customer.id);
    }
    @Test
    public void testListCustomers() throws Exception {
        // TODO: Test update also
        Map<String, String> projectSettings = new HashMap<>();
        projectSettings.put("amoCrmHost", amoCrmHost);
        projectSettings.put("amoCrmUser", amoCrmUser);
        projectSettings.put("amoCrmPassword", amoCrmPassword);
        OkHttpClient httpClient = amoCrmAccountService.getOkHttpClient();
        SetCustomer setCustomer = amoCrmCustomerService.createCustomer("John Doe");
        Response<SetCustomerResponse> setCustomerResponse = amoCrmCustomerService.setCustomer(setCustomer, projectSettings);
        assertEquals(setCustomerResponse.body().response.customers.add.customers.size(), 1);
        Response<ListCustomersResponse> listCustomersResponse = amoCrmCustomerService.list(projectSettings);
        assertEquals(listCustomersResponse.body().response.customers.size(), 1);

        SetCustomer deleteCustomer = new SetCustomer();
        deleteCustomer.request = new SetCustomerRequest();
        deleteCustomer.request.customers = new SetCustomerRequestCustomers();
        deleteCustomer.request.customers.delete = new ArrayList<>();
        deleteCustomer.request.customers.delete.add(setCustomerResponse.body().response.customers.add.customers.get(0).id);
        Response<SetCustomerResponse> deleteCustomerResponse = amoCrmCustomerService.setCustomer(deleteCustomer, projectSettings);

        Response<ListCustomersResponse> listCustomersResponse2 = amoCrmCustomerService.list(projectSettings);
        assertEquals(listCustomersResponse.body().response.customers.size(), 0);

    }
}