package xyz.youngzz.oauthexample

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {

    companion object {
        const val NAVER_CLIENT_ID = "9jTdD5GFabcAkROVA29k"
        const val NAVER_CLIENT_SECRET = "lvzxtIgaR6"
    }


    inner class SessionCallback : ISessionCallback {
        override fun onSessionOpenFailed(exception: KakaoException?) {
            exception?.let {
                it.printStackTrace()
            }
        }

        override fun onSessionOpened() {
            Log.i("OAuth_app", "onSessionOpened")
            requestMe()
        }

    }

    lateinit var callback: SessionCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            val info = packageManager.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("MY KEY HASH:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (e : PackageManager.NameNotFoundException) {

        } catch (e: NoSuchFieldException) {
        }


        callback = SessionCallback()
        Session.getCurrentSession().addCallback(callback)
        Session.getCurrentSession().checkAndImplicitOpen()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data))
            return

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun requestMe() {
        val keys = listOf(
                "properties.nickname",
                "properties.profile_image"
        )

        UserManagement.getInstance().me(keys, object : MeV2ResponseCallback() {
            override fun onSuccess(result: MeV2Response?) {
                result?.let {
                    Log.i("OAuth_app", "ID: ${it.id}")
                    Log.i("OAuth_app", "Nickname: ${it.nickname}")
                    Log.i("OAuth_app", "Profile Image: ${it.profileImagePath}")
                    GlideApp.with(this@MainActivity)
                            .load(it.profileImagePath)
                            .into(profileImageView)
                }
            }

            override fun onSessionClosed(errorResult: ErrorResult?) {
                Log.e("OAuth_app", errorResult?.errorMessage)
            }

        })
    }

}


@GlideModule
class ChatAppGlideModule : AppGlideModule()