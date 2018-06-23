package com.example.asus.bakingFrag.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BakingApi {

    public static final String BAKING_URL = "https://d17h27t6h515a5.cloudfront.net/";

    public static EndPoint getRequest(){
        return new Retrofit.Builder()
                .baseUrl(BAKING_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(EndPoint.class);
    }
}
