package xyz.youngzz.oauthexample

import android.app.Application
import android.content.Context
import com.kakao.auth.*

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


}