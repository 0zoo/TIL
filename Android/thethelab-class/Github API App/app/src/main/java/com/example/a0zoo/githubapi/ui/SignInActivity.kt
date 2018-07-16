package com.example.a0zoo.githubapi.ui


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.a0zoo.githubapi.R
import com.example.a0zoo.githubapi.api.AuthApi
import com.example.a0zoo.githubapi.api.authApi
import com.example.a0zoo.githubapi.api.provideGithubApi
import com.example.a0zoo.githubapi.api.updateToken
import com.example.a0zoo.githubapi.utils.enqueue
import kotlinx.android.synthetic.main.activity_sign_in.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.toast
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SignInActivity : AppCompatActivity() {

    companion object {
        val TAG = SignInActivity::class.java.simpleName

        const val CLIENT_ID = "2665b3d14a0c0fb47d25"
        const val CLIENT_SECRET = "1fb2914898c25210864d30120c30cb71bc49d33d"
    }

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

            // 인터넷 브라우저를 실행하는 Intent
            // => Custom Tabs: android-support-library
            val intentBuilder = CustomTabsIntent.Builder()
            //Open the Custom Tab
            intentBuilder.build().launchUrl(this,authUri)

        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        check(intent != null)
        check(intent?.data != null)

        val uri = intent?.data
        val code = uri?.getQueryParameter("code") ?: throw IllegalStateException("no code!!")

        getAccessToken(code)

    }

    private fun getAccessToken(code: String) {
        val call = authApi.getAccessToken(CLIENT_ID, CLIENT_SECRET, code)

        call.enqueue({
            it.body()?.let {
                Log.i(TAG, it.toString())

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





