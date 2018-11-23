package xyz.e0zoo.photogallery

import android.support.v4.app.Fragment

class PhotoGalleryActivity : SingleFragmentActivity() {

    override fun createFragment(): Fragment = PhotoGalleryFragment.newInstance()

}
