package xyz.youngzz.rxjava2retrofitexample.api.model

import com.google.gson.annotations.SerializedName

data class User(
        val login:String,
        @field:SerializedName("avatar_url")
        val avatarUrl: String
)