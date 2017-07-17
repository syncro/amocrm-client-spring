package com.amocrm.amocrmclient.service;

import com.amocrm.amocrmclient.entity.customer.list.LCResponseData;
import com.amocrm.amocrmclient.entity.customer.set.SCParam;
import com.amocrm.amocrmclient.entity.customer.set.SCRequest;
import com.amocrm.amocrmclient.entity.customer.set.SCRequestCustomers;
import com.amocrm.amocrmclient.entity.customer.set.SCResponseAddCustomer;
import com.amocrm.amocrmclient.entity.customer.set.SCResponseData;
import com.amocrm.amocrmclient.entity.customer.set.SCResponseDeleteCustomer;
import com.amocrm.amocrmclient.entity.lead.list.LLResponseData;
import com.amocrm.amocrmclient.entity.lead.set.SLParam;
import com.amocrm.amocrmclient.entity.lead.set.SLResponseData;

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
public class AmoCrmLeadServiceTest {

    @Autowired
    AmoCrmAccountService amoCrmAccountService;

    @Autowired
    AmoCrmLeadService amoCrmLeadService;

    @Value("${amocrm.host}")
    private String amoCrmHost;

    @Value("${amocrm.user}")
    private String amoCrmUser;

    @Value("${amocrm.password}")
    private String amoCrmPassword;

    @Test
    public void testSetLeadAndList() throws Exception {
        Map<String, String> projectSettings = new HashMap<>();
        projectSettings.put("amoCrmHost", amoCrmHost);
        projectSettings.put("amoCrmUser", amoCrmUser);
        projectSettings.put("amoCrmPassword", amoCrmPassword);
        SLParam setLead = amoCrmLeadService.createLead("Frodo Buggins", 100);
        Response<SLResponseData> setLeadResponse = amoCrmLeadService.setLead(setLead, projectSettings);
        assertEquals(setLeadResponse.body().response.leads.add.size(), 1);

        Response<LLResponseData> leadListResponse = amoCrmLeadService.list(projectSettings);
        assertTrue(leadListResponse.body().response.leads.size() > 0);

        Response<LLResponseData> queriedLeadListResponse = amoCrmLeadService.list(projectSettings, "fro");
        assertTrue(queriedLeadListResponse.body().response.leads.size() > 0);
    }

}