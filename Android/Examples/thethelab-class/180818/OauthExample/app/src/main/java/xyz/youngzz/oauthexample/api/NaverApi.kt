package xyz.youngzz.oauthexample.api

import retrofit2.Call
import retrofit2.http.GET
import xyz.youngzz.oauthexample.api.model.Response

interface NaverApi{
    @GET("v1/nid/me")
    fun getUserInfo(): Call<Response>

}