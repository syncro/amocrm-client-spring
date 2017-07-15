package com.amocrm.amocrmclient.service;

import com.amocrm.amocrmclient.entity.contact.set.SCResponseAdd;
import com.amocrm.amocrmclient.entity.contact.links.CLResponseData;
import com.amocrm.amocrmclient.entity.contact.set.SCParam;
import com.amocrm.amocrmclient.entity.contact.set.SCResponseData;

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
import static org.junit.Assert.assertNotEquals;


@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:serviceContext-test.xml")
public class AmoCrmContactServiceTest {

    @Autowired
    AmoCrmContactService amoCrmContactService;

    @Value("${amocrm.host}")
    private String amoCrmHost;

    @Value("${amocrm.user}")
    private String amoCrmUser;

    @Value("${amocrm.password}")
    private String amoCrmPassword;

/*    @Test
    public void testSetAndDeleteContact() throws Exception {
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

    }*/

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

    @Test
    public void testLinks() throws Exception {
        // TODO: Test update also
        Map<String, String> projectSettings = new HashMap<>();
        projectSettings.put("amoCrmHost", amoCrmHost);
        projectSettings.put("amoCrmUser", amoCrmUser);
        projectSettings.put("amoCrmPassword", amoCrmPassword);
        OkHttpClient httpClient = amoCrmContactService.getOkHttpClient();
        SCParam setContact = amoCrmContactService.createContact("John Doe");
        Response<SCResponseData> setContactResponse = amoCrmContactService.setContact(setContact, projectSettings);
        assertEquals(setContactResponse.body().response.contacts.add.size(), 1);

        SCResponseAdd contact = setContactResponse.body().response.contacts.add.get(0);

        Response<CLResponseData> listLinksResponse = amoCrmContactService.links(projectSettings);
        assertNotEquals(listLinksResponse.body().response.links.size(), 0);

    }
}