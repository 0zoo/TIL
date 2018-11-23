package xyz.e0zoo.criminalintent

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point

object PictureUtils {

    fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {
        //파일의 이미지 크기를 알아낸다.
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)

        val srcWidth: Float = options.outWidth.toFloat()
        val srcHeight: Float = options.outHeight.toFloat()

        // 얼마나 크기를 조정할지 파악한다.
        val inSampleSize = if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight)
                Math.round(srcHeight / destHeight)
            else
                Math.round(srcWidth / destWidth)
        } else 1

        BitmapFactory.Options().let {
            it.inSampleSize = inSampleSize
            return BitmapFactory.decodeFile(path, it) // Bitmap 생성

        }
    }

    fun getScaleBitmap(path: String, activity: Activity): Bitmap {
        // 화면 크기를 확인하고
        // 이미지를 화면 크기에 맞게 조정
        // ImageView는 항상 이 크기보다 작을 것
        val size = Point()
        activity.windowManager.defaultDisplay.getSize(size)
        return getScaledBitmap(path, size.x, size.y)
    }

}