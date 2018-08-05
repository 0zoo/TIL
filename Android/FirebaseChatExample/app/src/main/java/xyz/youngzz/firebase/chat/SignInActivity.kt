package xyz.youngzz.firebase.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.startActivity

class SignInActivity : AppCompatActivity() {

    companion object {
        const val RC_SIGN_IN = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // 1. providers 설정
        val providers: List<AuthUI.IdpConfig> = listOf(
                AuthUI.IdpConfig.GoogleBuilder().build()
        )

        googleSignInButton.setOnClickListener {
            // startActivity

            // 2. 로그인 액티비티 수행
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build()
                    , RC_SIGN_IN)
            // => onActivityResult
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 3. 로그인 결과 처리
        if (requestCode == RC_SIGN_IN) {

            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                user?.let {
                    Log.i("SignInActivity", user.uid)
                    startActivity<ChatActivity>()
                }
            } else {
                Log.e("SignInActivity", "Failed")
            }

        }
    }
}

/*
// startActivity<ChatActivity>()
// Extension Function : 수평확장을 위한 코틀린의 새로운 기능
inline fun<reified E .... > Context.startActivity2(){
    // E::java
    // => 자바의 제네릭은 소거 방식으로 정의되어 있다.

    val intent : Intent(this, E:: class.java)
    startActivity(intent)
}

*/













