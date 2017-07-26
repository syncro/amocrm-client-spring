package com.amocrm.amocrmclient.service;

import com.amocrm.amocrmclient.account.entity.current.ACData;
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

@RunWith(SpringRunner.class)
@ContextHierarchy({@ContextConfiguration(classes = {
}, initializers = ConfigFileApplicationContextInitializer.class)})
@TestPropertySource(locations="classpath:amocrm.properties")
public class AmoCrmAccountServiceTest {

    private AmoCrmAccountService amoCrmAccountService;

    @Value("${amocrm.host}")
    private String amoCrmHost;

    @Value("${amocrm.user}")
    private String amoCrmUser;

    @Value("${amocrm.password}")
    private String amoCrmPassword;

    @Autowired
    void setAmoCrmAccountService() {
        AmoCrmClientConfig config = new AmoCrmClientConfig(amoCrmHost, amoCrmUser, amoCrmPassword);
        amoCrmAccountService = new AmoCrmAccountService(config);
    }

    @Test
    public void testData() throws Exception {
        Response<ACData> response = amoCrmAccountService.data();
        assertEquals(amoCrmHost, "https://" + response.body().response.account.name + ".amocrm.com/");
    }

}