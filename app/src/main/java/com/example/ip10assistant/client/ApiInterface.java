package com.example.ip10assistant.client;

import com.example.ip10assistant.model.IpData;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("?format=json")
    Call<IpData> getIp();
}
