package xyz.youngzz.rxexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_sign_in.*


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

class SignInActivity : AppCompatActivity() {

    val viewModel = SignInModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //val emailObservable: BehaviorSubject<String> = BehaviorSubject.create()


    }
}
