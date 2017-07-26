package com.amocrm.amocrmclient.service;

import com.amocrm.amocrmclient.contact.entity.set.SCResponseAdd;
import com.amocrm.amocrmclient.contact.entity.links.CLResponseData;
import com.amocrm.amocrmclient.contact.entity.set.SCParam;
import com.amocrm.amocrmclient.contact.entity.set.SCResponseData;
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
import static org.junit.Assert.assertNotEquals;


@RunWith(SpringRunner.class)
@ContextHierarchy({@ContextConfiguration(classes = {
}, initializers = ConfigFileApplicationContextInitializer.class)})
@TestPropertySource(locations="classpath:amocrm.properties")
public class AmoCrmContactServiceTest {

    private AmoCrmContactService amoCrmContactService;

    @Value("${amocrm.host}")
    private String amoCrmHost;

    @Value("${amocrm.user}")
    private String amoCrmUser;

    @Value("${amocrm.password}")
    private String amoCrmPassword;


    @Autowired
    void setAmoCrmContactService() {
        AmoCrmClientConfig config = new AmoCrmClientConfig(amoCrmHost, amoCrmUser, amoCrmPassword);
        amoCrmContactService = new AmoCrmContactService(config);
    }


    @Test
    public void testSetAndDeleteContact() throws Exception {
        SCParam setContact = amoCrmContactService.createContact("John Doe");
        Response<SCResponseData> setContactResponse = amoCrmContactService.setContact(setContact);
        assertEquals(setContactResponse.body().response.contacts.add.size(), 1);

    }

/*    @Test
    public void testListContacts() throws Exception {
        // TODO: Test update also
        Map<String, String> projectSettings = new HashMap<>();
        projectSettings.put("amoCrmHost", amoCrmHost);
        projectSettings.put("amoCrmUser", amoCrmUser);
        projectSettings.put("amoCrmPassword", amoCrmPassword);
        OkHttpClient httpClient = amoCrmContactService.getOkHttpClient();
        SetContact setContact = amoCrmContactService.createContact("John Doe");
        Response<SetContactResponse> setContactResponse = amoCrmContactService.setContact(setContact, projectSettings);
        assertEquals(setContactResponse.body().response.contacts.add.size(), 1);

        AddContactResponseContactsAdd contact = setContactResponse.body().response.contacts.add.get(0);

        Response<ListContactsResponse> listContactsResponse = amoCrmContactService.list(projectSettings);
        assertNotEquals(listContactsResponse.body().response.contacts.size(), 0);

    }*/

    //@Test
    public void testLinks() throws Exception {
        // TODO: Test update also

        SCParam setContact = amoCrmContactService.createContact("John Doe");
        Response<SCResponseData> setContactResponse = amoCrmContactService.setContact(setContact);
        assertEquals(setContactResponse.body().response.contacts.add.size(), 1);

        SCResponseAdd contact = setContactResponse.body().response.contacts.add.get(0);

        Response<CLResponseData> listLinksResponse = amoCrmContactService.links();
        assertNotEquals(listLinksResponse.body().response.links.size(), 0);

    }
}