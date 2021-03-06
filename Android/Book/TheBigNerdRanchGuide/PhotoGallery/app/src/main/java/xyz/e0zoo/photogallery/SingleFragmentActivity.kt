package xyz.e0zoo.photogallery

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

abstract class SingleFragmentActivity : AppCompatActivity() {

    protected abstract fun createFragment(): Fragment

    @LayoutRes
    protected open fun getLayoutResId(): Int = R.layout.activity_fragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.fragmentContainer)
        if (fragment == null) {
            fragment = createFragment()
            fm.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit()
        }
    }
}

@GlideModule
class ChatAppGlideModule : AppGlideModule()