package xyz.youngzz.rxjava2retrofitexample.api

import retrofit2.Call
import retrofit2.http.GET
import xyz.youngzz.rxjava2retrofitexample.api.model.User

interface GithubApi{
    @GET("user")
    fun getUser() : Call<User>
}