package com.amarullah87.bakingapp.utilities;

import com.amarullah87.bakingapp.services.RestAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Baking App Project Android Fast Track Nanodegree - Created By Irfan Apandhi, September 2017
 * Configs is a Class that contain any URL/ variable to use later in another class
 */

public class Configs {

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";

    public static Retrofit getRetrofit(){
        Gson gson = new GsonBuilder().create();
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callFactory(httpClientBuilder.build())
                .build();
    }
}
