package com.example.a0zoo.githubapi.api

import com.example.a0zoo.githubapi.api.model.RepoSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {
    @GET("search/repositories")
    fun searchRepository(@Query("q") query: String): Call<RepoSearchResponse>
}
