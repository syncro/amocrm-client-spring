package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.entity.AuthResponse;
import com.amocrm.amocrmclient.entity.task.set.STAdd;
import com.amocrm.amocrmclient.entity.task.set.STParam;
import com.amocrm.amocrmclient.entity.task.set.STRequest;
import com.amocrm.amocrmclient.entity.task.set.STRequestTasks;
import com.amocrm.amocrmclient.entity.task.set.STResponse;
import com.amocrm.amocrmclient.iface.ITaskAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Component
public class AmoCrmTaskService extends AmoCrmService {

    private static final Logger logger = LoggerFactory.getLogger(AmoCrmTaskService.class);

    @Inject public AmoCrmTaskService(AmoCrmAuthService authService, AmoCrmAccountService amoCrmAccountService) {
        super(authService, amoCrmAccountService);
    }

    public STParam createTask(String text, long elementId, int elementType, int taskType, int completeTill) {

        STParam setTask = new STParam();
        setTask.request = new STRequest();
        setTask.request.tasks = new STRequestTasks();
        setTask.request.tasks.add = new ArrayList<>();
        STAdd addTask = new STAdd();
        addTask.text = text;
        addTask.elementId = elementId;
        addTask.elementType = elementType;
        addTask.taskType = taskType;
        addTask.completeTill = completeTill;

        setTask.request.tasks.add.add(addTask);

        return setTask;
    }

    public Response<STResponse> setTask(STParam setTask, Map<String, String> projectSettings) {

        OkHttpClient httpClient = getOkHttpClient();

        Call<AuthResponse> authResponse = authService.auth(httpClient, projectSettings.get("amoCrmHost"),
                projectSettings.get("amoCrmUser"),  projectSettings.get("amoCrmPassword"));

        Response response = null;
        try {
            response = authResponse.execute();
            if (response.isSuccessful()) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(projectSettings.get("amoCrmHost"))
                        .client(httpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                ITaskAPI taskAPI = retrofit.create(ITaskAPI.class);

                return taskAPI.setTask(setTask).execute();
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Error placing task", e);
        }
        return null;
    }

}
