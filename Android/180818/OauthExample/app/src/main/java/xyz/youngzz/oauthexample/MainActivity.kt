package xyz.youngzz.oauthexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import xyz.youngzz.oauthexample.module.GlideApp


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val type = intent.extras.getString("LOGIN")

        when(type){
            "KAKAO"-> requestMe()

        }

        logoutButton.setOnClickListener{
            onClickLogout(type)
        }


    }

    private fun onClickLogout(type : String) {
        when(type){
            "KAKAO"-> {
                UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
                    override fun onCompleteLogout() {
                        //redirectLoginActivity()
                        startActivity<SignInActivity>()
                    }
                })
            }

        }


    }

    private fun requestMe() {
        val keys = listOf(
                "properties.nickname",
                "properties.profile_image",
                "kakao_account.email"
        )

        UserManagement.getInstance().me(keys, object : MeV2ResponseCallback() {
            override fun onSuccess(result: MeV2Response?) {
                result?.let {
                    Log.i("OAuth_app", "ID: ${it.id}")
                    Log.i("OAuth_app", "Nickname: ${it.nickname}")
                    Log.i("OAuth_app", "Profile Image: ${it.profileImagePath}")
                    Log.i("OAuth_app", "Email: ${it.kakaoAccount.email}")


                    GlideApp.with(this@MainActivity)
                            .load(it.profileImagePath)
                            .into(profileImageView)

                    nameTextView.text = it.nickname

                }
            }

            override fun onSessionClosed(errorResult: ErrorResult?) {
                Log.e("OAuth_app", errorResult?.errorMessage)
            }

        })
    }




}

