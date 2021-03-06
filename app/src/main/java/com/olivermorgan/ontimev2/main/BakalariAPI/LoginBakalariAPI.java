// taken from https://github.com/vitSkalicky/lepsi-rozvrh/
package com.olivermorgan.ontimev2.main.BakalariAPI;


import retrofit2.Call;
import retrofit2.http.GET;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginBakalariAPI {

    @FormUrlEncoded
    @POST("api/login")
    Call<LoginResponse> firstLogin(@Field("client_id") String clientId, @Field("grant_type") String grantType, @Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("api/login")
    Call<LoginResponse> refreshLogin(@Field("client_id") String clientId, @Field("grant_type") String grantType, @Field("refresh_token") String refreshToken);

    @GET("api/3/user")
    Call<UserResponse> getUser();

}
