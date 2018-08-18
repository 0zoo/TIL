package xyz.youngzz.rxjava2retrofitexample.api

import android.content.Context
import android.preference.PreferenceManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val httpClient: OkHttpClient = OkHttpClient.Builder().apply {
    addInterceptor(loggingInterceptor)
}.build()

val authRetrofit = Retrofit.Builder().apply {
    baseUrl("https://github.com/")
    client(httpClient)
    addConverterFactory(GsonConverterFactory.create())
}.build()

val authApi : AuthApi = authRetrofit.create(AuthApi::class.java)






fun authHttpClient(context: Context) = OkHttpClient.Builder().apply {
    addInterceptor(loggingInterceptor)
    addInterceptor(AuthTokenInterceptor(context))
}.build()

fun githubApiRetrofit(context: Context) = Retrofit.Builder().apply {
    baseUrl("https://api.github.com/")
    client(authHttpClient(context))
    addConverterFactory(GsonConverterFactory.create())
}.build()

fun provideGithubApi(context: Context) = githubApiRetrofit(context).create(GithubApi::class.java)




const val KEY_AUTH_TOKEN = "xyz.youngzz.example.auth_token"

fun updateToken(context: Context, token: String){
    PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString("KEY_AUTH_TOKEN",token)
            .apply()
}


fun getToken(context: Context): String? = PreferenceManager.getDefaultSharedPreferences(context).getString("KEY_AUTH_TOKEN",null)


class AuthTokenInterceptor(private val context: Context) : Interceptor {
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