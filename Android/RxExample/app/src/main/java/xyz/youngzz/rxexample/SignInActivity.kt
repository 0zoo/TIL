package xyz.youngzz.rxexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_sign_in.*


data class SignInModel(val email : String, val password : String)

class SignInViewModel{
    var email = ""
    var password = ""
    val model = SignInModel("","")

    fun login(){
        //model.login(email, password )
    }
}

// Android Project
//  app
//    src
//      main/java/   - 제품 코드

//      test         - Model(안드로이드 의존성이 없는 모듈을 검증하기 위한 코드)
//      androidTest  - View/Activity(안드로이드에 의존성이 있는 모듈을 검증하기 위한 코드)



class SignInActivity : AppCompatActivity() {

    val viewModel = SignInModel("","")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        PublishSubject.create<SignInModel>()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()

        emailEditText.textChanges().subscribe{
            Log.i("SignInActivity",it.toString())
        }

    }
}
