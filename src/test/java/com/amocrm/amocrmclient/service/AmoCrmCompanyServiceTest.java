package com.amocrm.amocrmclient.service;

import com.amocrm.amocrmclient.entity.company.list.LCResponseData;
import com.amocrm.amocrmclient.entity.company.set.SCParam;
import com.amocrm.amocrmclient.entity.company.set.SCResponseData;

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
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:serviceContext-test.xml")
public class AmoCrmCompanyServiceTest {

    @Autowired
    AmoCrmCompanyService amoCrmCompanyService;

    @Value("${amocrm.host}")
    private String amoCrmHost;

    @Value("${amocrm.user}")
    private String amoCrmUser;

    @Value("${amocrm.password}")
    private String amoCrmPassword;

    @Test
    public void testCrateAndListCustomers() throws Exception {
        Map<String, String> projectSettings = new HashMap<>();
        projectSettings.put("amoCrmHost", amoCrmHost);
        projectSettings.put("amoCrmUser", amoCrmUser);
        projectSettings.put("amoCrmPassword", amoCrmPassword);

        SCParam setCompany = amoCrmCompanyService.createCompany("Some Company");

        Response<SCResponseData> setCompanyResponse = amoCrmCompanyService.setCompany(setCompany, projectSettings);
        assertEquals(setCompanyResponse.body().response.contacts.add.size(), 1);

        Response<LCResponseData> listCustomersResponse = amoCrmCompanyService.list(projectSettings);
        assertTrue(listCustomersResponse.body().response.contacts.size() > 0);

    }


}