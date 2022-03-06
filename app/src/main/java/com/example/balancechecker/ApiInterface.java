package com.example.balancechecker;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ApiInterface {
    @GET("api")
    Call<EtherPojo> getBalance(@QueryMap Map<String, String> options);

    @GET("data/price")
    Call<CurrencyPojo> getCurrencyRate(@QueryMap Map<String, String> options);
}
