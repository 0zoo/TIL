package com.example.a0zoo.githubapi.api

import com.example.a0zoo.githubapi.api.model.Auth
import retrofit2.Call
import retrofit2.http.*

// https://github.com
interface AuthApi {

    @FormUrlEncoded
    @POST("login/oauth/access_token")
    @Headers("Accept: application/json")
    fun getAccessToken(@Field("client_id") clientId: String,
                       @Field("client_secret") clientSecret: String,
                       @Field("code") code: String): Call<Auth>

}