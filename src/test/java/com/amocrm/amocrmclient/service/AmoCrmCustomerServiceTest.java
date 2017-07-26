package com.amocrm.amocrmclient.service;

import com.amocrm.amocrmclient.customer.entity.list.LCResponseData;
import com.amocrm.amocrmclient.customer.entity.set.SCResponseAddCustomer;
import com.amocrm.amocrmclient.customer.entity.set.SCParam;
import com.amocrm.amocrmclient.customer.entity.set.SCRequest;
import com.amocrm.amocrmclient.customer.entity.set.SCRequestCustomers;
import com.amocrm.amocrmclient.customer.entity.set.SCResponseData;
import com.amocrm.amocrmclient.customer.entity.set.SCResponseDeleteCustomer;
import com.amocrm.amocrmclient.service.configuration.AmoCrmClientConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@ContextHierarchy({@ContextConfiguration(classes = {
}, initializers = ConfigFileApplicationContextInitializer.class)})
@TestPropertySource(locations="classpath:amocrm.properties")
public class AmoCrmCustomerServiceTest {

    private AmoCrmCustomerService amoCrmCustomerService;

    @Value("${amocrm.host}")
    private String amoCrmHost;

    @Value("${amocrm.user}")
    private String amoCrmUser;

    @Value("${amocrm.password}")
    private String amoCrmPassword;


    @Autowired
    void setAmoCrmCustomerService() {
        AmoCrmClientConfig config = new AmoCrmClientConfig(amoCrmHost, amoCrmUser, amoCrmPassword);
        amoCrmCustomerService = new AmoCrmCustomerService(config);
    }

    @Test
    public void testCrateAndListCustomers() throws Exception {

        SCParam setCustomer = amoCrmCustomerService.createCustomer("John Doe");

        Response<SCResponseData> setCustomerResponse = amoCrmCustomerService.setCustomer(setCustomer);
        assertEquals(setCustomerResponse.body().response.customers.add.customers.size(), 1);

        Response<LCResponseData> listCustomersResponse = amoCrmCustomerService.list();
        assertTrue(listCustomersResponse.body().response.customers.size() > 0);

    }

    @Test
    public void testSetAndDeleteCustomer() throws Exception {
        // delete is not working due to some 282 error, guess this is: functional disabled by administrator

        SCParam setCustomer = amoCrmCustomerService.createCustomer("John Doe");
        Response<SCResponseData> setCustomerResponse = amoCrmCustomerService.setCustomer(setCustomer);
        assertEquals(setCustomerResponse.body().response.customers.add.customers.size(), 1);

        SCResponseAddCustomer customer = setCustomerResponse.body().response.customers.add.customers.get(0);

        SCParam deleteCustomer = new SCParam();
        deleteCustomer.request = new SCRequest();
        deleteCustomer.request.customers = new SCRequestCustomers();
        deleteCustomer.request.customers.add = new ArrayList<>();
        deleteCustomer.request.customers.delete = new ArrayList<>();
        deleteCustomer.request.customers.delete.add(customer.id);
        Response<SCResponseData> deleteCustomerResponse = amoCrmCustomerService.setCustomer(deleteCustomer);

        SCResponseDeleteCustomer deletedCustomer = deleteCustomerResponse.body().response.customers.delete.customers.get(String.valueOf(customer.id));
        assertEquals(deletedCustomer.id, customer.id);
    }

}