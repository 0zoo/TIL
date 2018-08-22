package xyz.youngzz.oauthexample.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

fun naverhttpClient(context: Context, accessToken : String): OkHttpClient = OkHttpClient.Builder().apply {
    addInterceptor(loggingInterceptor)
    addInterceptor(AccessTokenInterceptor(context, accessToken))
}.build()

fun naverApiRetrofit(context: Context, accessToken : String) = Retrofit.Builder().apply {
    baseUrl("https://openapi.naver.com/")
    client(naverhttpClient(context, accessToken))
    addConverterFactory(GsonConverterFactory.create())
}.build()

fun provideNaverApi(context: Context, accessToken : String): NaverApi = naverApiRetrofit(context, accessToken).create(NaverApi::class.java)


class AccessTokenInterceptor(private val context: Context, private val accessToken : String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val request = original.newBuilder().apply {

            addHeader("Authorization", "Bearer $accessToken")

        }.build()

        return chain.proceed(request)
    }
}

