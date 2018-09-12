package xyz.youngzz.rxjava2retrofitexample.view

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.util.Log
import kotlinx.android.synthetic.main.activity_sign_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xyz.youngzz.rxjava2retrofitexample.R
import xyz.youngzz.rxjava2retrofitexample.api.*
import xyz.youngzz.rxjava2retrofitexample.api.model.Auth
import xyz.youngzz.rxjava2retrofitexample.api.model.User

class SignInActivity : AppCompatActivity() {

    companion object {
        const val CLIENT_ID = "2665b3d14a0c0fb47d25"
        const val CLIENT_SECRET = "1fb2914898c25210864d30120c30cb71bc49d33d"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signInButton.setOnClickListener {
            // https://github.com/login/oauth/authorize
            val authUri = Uri.Builder().scheme("https")
                    .authority("github.com")
                    .appendPath("login")
                    .appendPath("oauth")
                    .appendPath("authorize")
                    .appendQueryParameter("client_id", CLIENT_ID)
                    .build()

            CustomTabsIntent.Builder().build().launchUrl(this, authUri)

        }
    }

    // 내가 지정한 주소로 callback
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        check(intent != null)
        check(intent?.data != null)

        val uri = intent?.data
        val code = uri?.getQueryParameter("code") ?: throw IllegalStateException("no code")

        Log.i("SignInActivity", code)

        getAccessToken(code)


    }

    private fun getAccessToken(code: String) {

        val call = authApi.getAccessToken(CLIENT_ID, CLIENT_SECRET, code)

        call.enqueue(object : Callback<Auth> {
            override fun onFailure(call: Call<Auth>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<Auth>, response: Response<Auth>) {
                response.body()?.let {
                    updateToken(this@SignInActivity, it.accessToken)
                    Log.i("SignInActivity", it.toString())

                    val githubApiCall = provideGithubApi(this@SignInActivity).getUser()

                    githubApiCall.enqueue(object : Callback<User> {
                        override fun onFailure(call: Call<User>?, t: Throwable?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onResponse(call: Call<User>?, response: Response<User>) {
                            response.body()?.let {
                                Log.i("SignInActivity", it.toString())
                            }
                        }

                    })
                }
            }

        })
    }


}


