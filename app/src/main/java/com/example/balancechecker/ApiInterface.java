package com.example.balancechecker;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("api?module=account&action=balance&address=0xd346a26d9787093e997e8bcb2d33a469d7c1c1ee&tag=latest&apikey=S58CQ2HR2W9JJS3JWZ6SB5XHAF1K2UHPF7")
    Call<PostPojo> getBalance();
}
