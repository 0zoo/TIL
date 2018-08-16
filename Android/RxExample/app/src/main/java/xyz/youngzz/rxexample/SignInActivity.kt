package xyz.youngzz.rxexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity


class SignInModel{
    fun login(email: String, password : String){
        //
    }
}

class SignInViewModel{
    var email = ""
    var password = ""
    val model = SignInModel()

    fun login(){
        model.login(email, password )
    }
}

// Android Project
//  app
//    src
//      main/java/   - 제품 코드

//      test         - Model(안드로이드 의존성이 없는 모듈을 검증하기 위한 코드)
//      androidTest  - View/Activity(안드로이드에 의존성이 있는 모듈을 검증하기 위한 코드)



class SignInActivity : AppCompatActivity() {

    val viewModel = SignInModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //val emailObservable: BehaviorSubject<String> = BehaviorSubject.create()


    }
}
