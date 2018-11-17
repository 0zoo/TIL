package xyz.e0zoo.nerdlauncher

import androidx.fragment.app.Fragment

class NerdLauncherActivity : SingleFragmentActivity() {

    override fun createFragment(): Fragment = NerdLauncherFragment.newInstance()

}
