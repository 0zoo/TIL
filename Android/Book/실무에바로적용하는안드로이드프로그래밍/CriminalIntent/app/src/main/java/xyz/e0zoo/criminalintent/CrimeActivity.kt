package xyz.e0zoo.criminalintent

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log

class CrimeActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime)

        Log.i("@@@",Crime().toString())


    }
}
