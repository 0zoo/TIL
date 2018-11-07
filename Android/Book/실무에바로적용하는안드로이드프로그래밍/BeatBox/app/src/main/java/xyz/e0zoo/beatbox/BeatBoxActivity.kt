package xyz.e0zoo.beatbox

import android.support.v4.app.Fragment

class BeatBoxActivity : SingleFragmentActivity() {

    override fun createFragment(): Fragment  = BeatBoxFragment.newInstance()
}
