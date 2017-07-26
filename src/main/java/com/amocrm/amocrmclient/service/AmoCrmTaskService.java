package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.service.configuration.AmoCrmClientConfig;
import com.amocrm.amocrmclient.task.TaskClient;
import com.amocrm.amocrmclient.task.TaskClientBuilder;
import com.amocrm.amocrmclient.task.entity.set.STParam;
import com.amocrm.amocrmclient.task.entity.set.STResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Response;

@Component
public class AmoCrmTaskService extends AmoCrmService {

    private static final Logger logger = LoggerFactory.getLogger(AmoCrmTaskService.class);

    private TaskClient taskClient;

    @Inject
    public AmoCrmTaskService(AmoCrmClientConfig config) {
        this.taskClient = new TaskClientBuilder()
                .baseUrl(config.getBaseUrl())
                .login(config.getLogin())
                .passwordHash(config.getPasswordHash())
                .build();
    }

    public STParam createTask(String text, long elementId, int elementType, int taskType, int completeTill) {

        return taskClient.createTask(text, elementId, elementType, taskType, completeTill);
    }

    public Response<STResponse> setTask(STParam setTask) {

        try {
            return taskClient.setTask(setTask);
        } catch (IOException e) {
            logger.error("Error setting task", e);
        }

        return null;
    }

}
