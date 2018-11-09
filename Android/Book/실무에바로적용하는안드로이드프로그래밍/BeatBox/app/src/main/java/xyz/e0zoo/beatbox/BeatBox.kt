package xyz.e0zoo.beatbox

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import java.io.IOException

class BeatBox(context: Context) {

    companion object {
        const val TAG = "BeatBox"
        const val SOUNDS_FOLDER = "sample_sounds"
        const val MAX_SOUNDS = 5
    }

    private val mAssets = context.assets
    private val mSounds = arrayListOf<Sound>()
    private val mSoundPool by lazy {
        SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0)
    }

    init {
        loadSounds()
    }

    @Throws(IOException::class)
    private fun load(sound: Sound) {
        val afd = mAssets.openFd(sound.assetPath)
        sound.soundId = mSoundPool.load(afd, 1)
        Log.i(TAG, sound.soundId.toString())
    }

    private fun loadSounds() {

        val soundNames: Array<String> = try {
            mAssets.list(SOUNDS_FOLDER)
        } catch (ioe: IOException) {
            Log.e(TAG, "Could not list assets", ioe)
            return
        }

        Log.i(TAG, "Found ${soundNames.size} sounds")

        for (filename in soundNames) {

            try {
                val assetPath = "$SOUNDS_FOLDER/$filename"
                val sound = Sound(assetPath)
                load(sound)
                mSounds.add(sound)

            } catch (ioe: IOException) {
                Log.e(TAG, "Could not load sound $filename", ioe)
            }
        }

    }

    fun play(sound: Sound) {
        val soundId = sound.soundId
        soundId?.let {
            mSoundPool.play(it, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    fun release() = mSoundPool.release()

    fun getSounds(): List<Sound> = mSounds
}