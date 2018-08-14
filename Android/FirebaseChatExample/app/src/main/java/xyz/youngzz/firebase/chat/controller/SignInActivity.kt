package xyz.youngzz.firebase.chat.controller
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.jetbrains.anko.startActivity
import xyz.youngzz.firebase.chat.R


// Firebase의 서비스 계정 파일은 절대 공개된 Github에 올리면 안됩니다.
//  => app/.gitignore에 google-services.json을 추가.

// 1) Connect your app to Firebase
//    app/google-services.json을 복사

// 2) Add Firebase Authentication to your app
//    build.gradle에 의존성을 추가

class SignInActivity : AppCompatActivity() {
    companion object {
        const val RC_SIGN_IN = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // 1. Provider 설정
        val providers: List<AuthUI.IdpConfig> = listOf(
                AuthUI.IdpConfig.GoogleBuilder().build()
        )

        googleSignInButton.setOnClickListener {
            // 2. 로그인 액티비티 수행
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(), RC_SIGN_IN)
            //  => onActivityResult
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 3. 로그인 결과 처리
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser

                user?.let {
                    // Log.i("SignInActivity", user.uid)
                    // Anko commons
                    startActivity<ChatActivity>()
                }
            } else {
                Log.e("SignInActivity", "Sign in Failed")
            }
        }
    }
}

/*
    List<String> s = new ArrayList<>();
    s.add("hello");
    // s.add(42);  // error!
    //-------------
    List s = new ArrayList();
    s.add("hello");
    // Generic
    //  1) 코드 생성        => C++
    //  2) 컴파일 타임 체크   => Java
*/


// startActivity<ChatActivity>()
// Extension Function => 수평 확장을 위한 코틀린의 새로운 기능
/*
inline fun<reified E: Activity> Context.startActivity() {
    // E::class
    //  => Java의 Generic은 소거 방식으로 구현되어 있다.
    val intent = Intent(this, E::class.java)
    startActivity(intent)
}

inline fun <reified T: Activity> Context.startActivity(vararg params: Pair<String, Any?>) =
        AnkoInternals.internalStartActivity(this, T::class.java, params)
*/














