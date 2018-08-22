package xyz.youngzz.oauthexample.api.model

import com.google.gson.annotations.SerializedName

data class Response(
        val resultcode : String,
        val message : String,
        val response : User
)


data class User(
        val name:String,
        @field:SerializedName("profile_image")
        val profileImage: String,
        val nickname: String,
        val email : String
)


