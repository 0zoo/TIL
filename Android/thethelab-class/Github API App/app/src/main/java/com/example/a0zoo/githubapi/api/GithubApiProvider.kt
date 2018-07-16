package com.example.a0zoo.githubapi.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 1. Retrofit의 Client는 자바에서는 '싱글톤'으로 많이 만들었습니다.
// 2. Kotlin에서는 object을 통해서 싱글톤을 쉽게 만들 수 있다.
// 3. Kotlin에서는 전역 메소드 또는 전역 프로퍼티를 사용할 수 있다.


val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val httpClient: OkHttpClient = OkHttpClient.Builder().apply {
    addInterceptor(loggingInterceptor)
}.build()

val authApi: AuthApi = Retrofit.Builder().apply {
    baseUrl("https://github.com/")
    client(httpClient)
    addConverterFactory(GsonConverterFactory.create())
}.build().create(AuthApi::class.java)


// Interceptor == Middleware
class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val request = original.newBuilder().apply {

            getToken(context)?.let { token ->
                addHeader("Authorization", "bearer $token")
            }

        }.build()

        return chain.proceed(request)
    }
}


fun authHttpClient(context: Context) = OkHttpClient.Builder().apply {
    addInterceptor(loggingInterceptor)
    addInterceptor(AuthInterceptor(context))
}.build()!!


fun provideGithubApi(context: Context) = Retrofit.Builder().apply {
    baseUrl("https://api.github.com/")
    client(authHttpClient(context))
    addConverterFactory(GsonConverterFactory.create())
}.build().create(GithubApi::class.java)!!

