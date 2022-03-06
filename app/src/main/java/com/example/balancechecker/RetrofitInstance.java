package com.example.balancechecker;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit, retrofit2;
    private static final String BASEURL = "https://api.etherscan.io/";
    private static final String BASEURL2 = "https://min-api.cryptocompare.com/";

    public static  Retrofit getRetrofit(){
        if(retrofit==null){
            retrofit = new Retrofit.Builder().
                    baseUrl(BASEURL).
                    addConverterFactory(GsonConverterFactory.create()).
                    build();
        }
        return retrofit;
    }
    public static  Retrofit getRetrofit2(){
        if(retrofit2==null){
            retrofit2 = new Retrofit.Builder().
                    baseUrl(BASEURL2).
                    addConverterFactory(GsonConverterFactory.create()).
                    build();
        }
        return retrofit2;
    }
}
