package xyz.e0zoo.criminalintent

import android.support.v4.app.Fragment

class CrimeActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment = CrimeFragment()
}
