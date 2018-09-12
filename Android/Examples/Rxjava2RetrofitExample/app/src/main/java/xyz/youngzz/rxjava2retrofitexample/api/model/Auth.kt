package xyz.youngzz.rxjava2retrofitexample.api.model

import com.google.gson.annotations.SerializedName

data class Auth(
        @field:SerializedName("access_token")
        val accessToken: String,
        @field:SerializedName("token_type")
        val tokenType: String)
