package com.amocrm.amocrmclient.service;

import com.amocrm.amocrmclient.company.entity.list.LCResponseData;
import com.amocrm.amocrmclient.company.entity.set.SCParam;
import com.amocrm.amocrmclient.company.entity.set.SCResponseData;
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

import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@ContextHierarchy({@ContextConfiguration(classes = {
}, initializers = ConfigFileApplicationContextInitializer.class)})
@TestPropertySource(locations="classpath:amocrm.properties")
public class AmoCrmCompanyServiceTest {

    private AmoCrmCompanyService amoCrmCompanyService;

    @Value("${amocrm.host}")
    private String amoCrmHost;

    @Value("${amocrm.user}")
    private String amoCrmUser;

    @Value("${amocrm.password}")
    private String amoCrmPassword;

    @Autowired
    void setAmoCrmCompanyService() {
        AmoCrmClientConfig config = new AmoCrmClientConfig(amoCrmHost, amoCrmUser, amoCrmPassword);
        amoCrmCompanyService = new AmoCrmCompanyService(config);
    }

    @Test
    public void testCrateAndListCustomers() throws Exception {

        SCParam setCompany = amoCrmCompanyService.createCompany("Some Company");

        Response<SCResponseData> setCompanyResponse = amoCrmCompanyService.setCompany(setCompany);
        assertEquals(setCompanyResponse.body().response.contacts.add.size(), 1);

        Response<LCResponseData> listCustomersResponse = amoCrmCompanyService.list();
        assertTrue(listCustomersResponse.body().response.contacts.size() > 0);

    }


}