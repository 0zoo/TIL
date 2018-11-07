package xyz.e0zoo.beatbox

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.fragment_beat_box.view.*
import kotlinx.android.synthetic.main.list_item_sound.view.*

class BeatBoxFragment : Fragment() {
    companion object {
        fun newInstance(): BeatBoxFragment = BeatBoxFragment()
    }

    private val mBeatBox: BeatBox by lazy {
        BeatBox(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_beat_box, container, false)

        view.run {
            recyclerView.layoutManager = GridLayoutManager(requireActivity(), 3)
            recyclerView.adapter = SoundAdapter(mBeatBox.getSounds())
        }
        return view
    }


    private class SoundHolder(inflater: LayoutInflater, container: ViewGroup?)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_sound, container, false)) {

        private val mButton: Button = itemView.soundButton
        private lateinit var mSound: Sound

        fun bindSound(sound: Sound){
            mSound = sound
            mButton.text = mSound.name
        }

    }



    private class SoundAdapter(val mSounds: List<Sound>) : RecyclerView.Adapter<SoundHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundHolder {
            val inflater = LayoutInflater.from(parent.context)
            return SoundHolder(inflater, parent)
        }

        override fun getItemCount(): Int = mSounds.size

        override fun onBindViewHolder(holder: SoundHolder, position: Int) {
            val sound = mSounds[position]
            holder.bindSound(sound)
        }

    }
}