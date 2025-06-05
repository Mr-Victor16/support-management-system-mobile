package com.example.support_management_system_mobile.auth;

import com.example.support_management_system_mobile.api.APIService;
import com.example.support_management_system_mobile.utils.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static final String BASE_URL = "http://10.0.2.2:8080";
    private static Retrofit retrofit = null;

    public static APIService getAPIService() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(APIService.class);
    }
}
