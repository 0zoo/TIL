package xyz.e0zoo.sunset

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator

class SunsetFragment : Fragment() {

    private lateinit var mSceneView: View
    private lateinit var mSunView: View
    private lateinit var mSkyView: View

    private val mBlueSkyColor: Int by lazy { ContextCompat.getColor(requireContext(), R.color.blue_sky) }
    private val mSunsetSkyColor: Int by lazy { ContextCompat.getColor(requireContext(), R.color.sunset_sky) }
    private val mNightSkyColor: Int by lazy { ContextCompat.getColor(requireContext(), R.color.night_sky) }


    companion object {
        fun newInstance() = SunsetFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sunset, container, false)
        mSceneView = view
        mSunView = view.findViewById(R.id.sun)
        mSkyView = view.findViewById(R.id.sky)

        mSceneView.setOnClickListener {
            startAnimation()
        }

        return view
    }

    private fun startAnimation() {
        val sunYStart: Float = mSunView.top.toFloat()
        val sunYEnd: Float = mSkyView.height.toFloat()
        val heightAnimator: ObjectAnimator = ObjectAnimator
            .ofFloat(mSunView, "y", sunYStart, sunYEnd)
            .setDuration(3000)
            .apply {
                interpolator = AccelerateInterpolator()
            }

        heightAnimator.start()

        val sunsetSkyAnimator = ObjectAnimator
            .ofInt(mSkyView, "backgroundColor", mBlueSkyColor, mSunsetSkyColor)
            .setDuration(3000)
            .apply {
                setEvaluator(ArgbEvaluator())
            }

        sunsetSkyAnimator.start()

    }

}

