package xyz.e0zoo.photogallery

import android.os.HandlerThread
import android.util.Log

class ThumbnailDownloader<T>(private val TAG: String = "ThumbnailDownloader") : HandlerThread(TAG) {
    fun queueThumbnail(target: T, url: String?) {
        Log.i(TAG, "Got a URL: $url")
    }
}
