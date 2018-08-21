package xyz.youngzz.oauthexample

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.util.exception.KakaoException
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.startActivity


class SignInActivity : AppCompatActivity() {

    companion object {
        const val NAVER_CLIENT_ID = "9jTdD5GFabcAkROVA29k"
        const val NAVER_CLIENT_SECRET = "lvzxtIgaR6"
    }


    inner class SessionCallback : ISessionCallback {
        override fun onSessionOpenFailed(exception: KakaoException?) {
            exception?.printStackTrace()
        }

        override fun onSessionOpened() {
            Log.i("OAuth_app", "onSessionOpened")
            startActivity<MainActivity>("LOGIN" to "KAKAO")

        }

    }

    lateinit var callback: SessionCallback
    lateinit var mOAuthLoginModule: OAuthLogin

    private val mOAuthLoginHandler = @SuppressLint("HandlerLeak")
    object : OAuthLoginHandler(){
        override fun run(success: Boolean) {
            if (success) {
                val accessToken = mOAuthLoginModule.getAccessToken(baseContext)
                val refreshToken = mOAuthLoginModule.getRefreshToken(baseContext)
                val expiresAt = mOAuthLoginModule.getExpiresAt(baseContext)
                val tokenType = mOAuthLoginModule.getTokenType(baseContext)

                Log.i("NAVER",accessToken)
            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        // kakao
        callback = SessionCallback()
        Session.getCurrentSession().addCallback(callback)
        Session.getCurrentSession().checkAndImplicitOpen()



        //naver
        mOAuthLoginModule = OAuthLogin.getInstance()
        mOAuthLoginModule.init(this, NAVER_CLIENT_ID, NAVER_CLIENT_SECRET,"TEST")

        naverButton.setOAuthLoginHandler(mOAuthLoginHandler)

        //mOAuthLoginModule.startOauthLoginActivity(this, mOAuthLoginHandler)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data))
            return

        super.onActivityResult(requestCode, resultCode, data)
    }







}

