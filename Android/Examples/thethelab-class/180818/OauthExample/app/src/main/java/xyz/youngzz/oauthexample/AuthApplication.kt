package xyz.youngzz.oauthexample

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.kakao.auth.*
import java.security.MessageDigest

class AuthApplication : Application(){

    private class KakaoSDKAdapter(val context: Context) : KakaoAdapter() {
        override fun getApplicationConfig(): IApplicationConfig = IApplicationConfig { context }

        override fun getSessionConfig(): ISessionConfig = object : ISessionConfig {
            override fun isSaveFormData() = true
            override fun getAuthTypes(): Array<AuthType> = arrayOf(AuthType.KAKAO_LOGIN_ALL)
            override fun isSecureMode(): Boolean = false
            override fun getApprovalType(): ApprovalType = ApprovalType.INDIVIDUAL
            override fun isUsingWebviewTimer() = false

        }

    }


    override fun onCreate() {
        super.onCreate()

        KakaoSDK.init(KakaoSDKAdapter(this))
    }

    private fun getKeyHash(){

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

    }

}

