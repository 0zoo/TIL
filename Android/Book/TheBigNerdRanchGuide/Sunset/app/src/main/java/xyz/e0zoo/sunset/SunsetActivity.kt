package xyz.e0zoo.sunset

class SunsetActivity : SingleFragmentActivity() {
    override fun createFragment() = SunsetFragment.newInstance()
}
