package com.example.balancechecker;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ApiInterface {
    @GET("api")
    Call<PostPojo> getBalance(@QueryMap Map<String, String> options);
}
