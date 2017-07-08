package com.amocrm.amocrmclient.service;


import com.amocrm.amocrmclient.entity.AuthResponse;
import com.amocrm.amocrmclient.entity.task.AddTask;
import com.amocrm.amocrmclient.entity.task.SetTask;
import com.amocrm.amocrmclient.entity.task.SetTaskRequest;
import com.amocrm.amocrmclient.entity.task.SetTaskRequestTasks;
import com.amocrm.amocrmclient.entity.task.SetTaskResponse;
import com.amocrm.amocrmclient.iface.ITaskAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Component
public class AmoCrmTaskService {

    private static final Logger logger = LoggerFactory.getLogger(AmoCrmTaskService.class);

    AmoCrmAuthService authService;

    @Inject public AmoCrmTaskService(AmoCrmAuthService authService) {
        this.authService = authService;
    }


    public SetTask createTask(String text, long elementId, int elementType, int taskType, int completeTill) {

        SetTask setTask = new SetTask();
        setTask.request = new SetTaskRequest();
        setTask.request.tasks = new SetTaskRequestTasks();
        setTask.request.tasks.add = new ArrayList<>();
        AddTask addTask = new AddTask();
        addTask.text = text;
        addTask.elementId = elementId;
        addTask.elementType = elementType;
        addTask.taskType = taskType;
        addTask.completeTill = completeTill;

        setTask.request.tasks.add.add(addTask);

        return setTask;
    }

    public Response<SetTaskResponse> setTask(SetTask setTask, Map<String, String> projectSettings) {

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

    private OkHttpClient getOkHttpClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(logging);

        CookieHandler cookieHandler = new CookieManager();
        JavaNetCookieJar jncj = new JavaNetCookieJar(cookieHandler);

        httpClientBuilder.cookieJar(jncj);
        httpClientBuilder.build();

        return httpClientBuilder.build();
    }
}
