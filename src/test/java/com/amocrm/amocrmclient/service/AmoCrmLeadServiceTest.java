package com.amocrm.amocrmclient.service;

import com.amocrm.amocrmclient.lead.entity.list.LLResponseData;
import com.amocrm.amocrmclient.lead.entity.set.SLParam;
import com.amocrm.amocrmclient.lead.entity.set.SLResponseData;
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
public class AmoCrmLeadServiceTest {

    private AmoCrmLeadService amoCrmLeadService;

    @Value("${amocrm.host}")
    private String amoCrmHost;

    @Value("${amocrm.user}")
    private String amoCrmUser;

    @Value("${amocrm.password}")
    private String amoCrmPassword;

    @Autowired
    void setAmoCrmLeadService() {
        AmoCrmClientConfig config = new AmoCrmClientConfig(amoCrmHost, amoCrmUser, amoCrmPassword);
        amoCrmLeadService = new AmoCrmLeadService(config);
    }

    @Test
    public void testSetLeadAndList() throws Exception {
        SLParam setLead = amoCrmLeadService.createLead("Frodo Buggins", 100);
        Response<SLResponseData> setLeadResponse = amoCrmLeadService.setLead(setLead);
        assertEquals(setLeadResponse.body().response.leads.add.size(), 1);

        Response<LLResponseData> leadListResponse = amoCrmLeadService.list();
        assertTrue(leadListResponse.body().response.leads.size() > 0);

        Response<LLResponseData> queriedLeadListResponse = amoCrmLeadService.list("fro");
        assertTrue(queriedLeadListResponse.body().response.leads.size() > 0);
    }

}