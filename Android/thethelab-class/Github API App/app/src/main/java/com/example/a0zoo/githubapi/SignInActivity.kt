package com.example.a0zoo.githubapi


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.a0zoo.githubapi.api.authApi
import com.example.a0zoo.githubapi.api.provideGithubApi
import com.example.a0zoo.githubapi.api.updateToken
import kotlinx.android.synthetic.main.activity_sign_in.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// Retrofit + OkHttp + Gson
//  : HTTP Request를 사용하는 데 반복되는 코드를 없앨 수 있다.
class SignInActivity : AppCompatActivity() {

    companion object {
        // const val TAG = "SignInActivity"
        val TAG = SignInActivity::class.java.simpleName

        const val CLIENT_ID = "2665b3d14a0c0fb47d25"
        const val CLIENT_SECRET = "1fb2914898c25210864d30120c30cb71bc49d33d"
    }

    // apply, let: 수신 객체 지정 람다
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signInButton.setOnClickListener {
            // https://github.com/login/oauth/authorize?client_id=XXX
            val authUri = Uri.Builder().scheme("https")
                    .authority("github.com")
                    .appendPath("login")
                    .appendPath("oauth")
                    .appendPath("authorize")
                    .appendQueryParameter("client_id", CLIENT_ID)
                    .build()

            // toast(authUri.toString())

            // 인터넷 브라우저를 실행하는 Intent
            // => Custom Tabs: android-support-library
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(this, authUri)
        }
    }

    // 기존의 액티비티가 다시 사용될 경우...
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        toast("onNewIntent")
        check(intent != null)
        check(intent?.data != null)

        val uri = intent?.data
        val code = uri?.getQueryParameter("code") ?: throw IllegalStateException("no code!!")

        getAccessToken(code)
    }


    private fun getAccessToken(code: String) {
        Log.i(TAG, "getAccessToken: $code")

        val call = authApi.getAccessToken(CLIENT_ID, CLIENT_SECRET, code)

        /*
        call.enqueue(object : Callback<Auth> {
            override fun onFailure(call: Call<Auth>, t: Throwable) {
                toast(t.message.toString())
            }
            override fun onResponse(call: Call<Auth>, response: Response<Auth>) {
                response.body()?.let {
                    toast(it.toString())
                }
            }
        })
        */

        call.enqueue({
            it.body()?.let {
                toast(it.toString())
                // Log.i(TAG, it.toString())

                updateToken(this, it.accessToken)

                val githubApiCall = provideGithubApi(this).searchRepository("hello")
                githubApiCall.enqueue({
                    it.body()?.let {
                        Log.i(TAG, "total_count: ${it.totalCount}")
                        Log.i(TAG, it.items.toString())
                    }

                }, {

                })


            }
        }, {
            toast(it.message.toString())
        })

    }
}

fun <T> Call<T>.enqueue(success: (response: Response<T>) -> Unit, failure: (t: Throwable) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) = failure(t)
        override fun onResponse(call: Call<T>, response: Response<T>) = success(response)
    })
}
