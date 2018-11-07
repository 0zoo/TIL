package xyz.e0zoo.beatbox

import android.content.Context
import android.util.Log
import java.io.IOException

class BeatBox(context: Context) {

    companion object {
        const val TAG = "BeatBox"
        const val SOUNDS_FOLDER = "sample_sounds"
    }

    private val mAssets = context.assets
    private val mSounds = arrayListOf<Sound>()

    init {
        loadSounds()
    }

    private fun loadSounds() {

        val soundNames = mAssets.list(SOUNDS_FOLDER) ?: throw IOException("Could not list assets")

        Log.i(TAG, "Found ${soundNames.size} sounds")

        for (filename in soundNames) {
            val assetPath = "$SOUNDS_FOLDER/$filename"
            val sound = Sound(assetPath)
            mSounds.add(sound)
        }

    }

    fun getSounds(): List<Sound> = mSounds
}