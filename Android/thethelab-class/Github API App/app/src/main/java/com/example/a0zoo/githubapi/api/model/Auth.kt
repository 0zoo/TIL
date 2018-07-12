package com.example.a0zoo.githubapi.api.model

import com.google.gson.annotations.SerializedName

data class Auth(
        @field:SerializedName("access_token")
        val accessToken: String,
        @field:SerializedName("token_type")
        val tokenType: String) {
}

data class GithubRepo(@field:SerializedName("full_name") val fullName: String)

data class RepoSearchResponse(@field:SerializedName("total_count") val totalCount: Int,
                              val items: List<GithubRepo>)