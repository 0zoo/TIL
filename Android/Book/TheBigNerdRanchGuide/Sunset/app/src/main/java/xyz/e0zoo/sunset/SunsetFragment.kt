package xyz.e0zoo.sunset

import android.animation.AnimatorSet
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

    private var flag = false


    companion object {
        fun newInstance() = SunsetFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sunset, container, false)
        mSceneView = view
        mSunView = view.findViewById(R.id.sun)
        mSkyView = view.findViewById(R.id.sky)

        mSceneView.setOnClickListener {
            if(flag)
                reverseAnimation()
            else
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

        //heightAnimator.start()

        val sunsetSkyAnimator = ObjectAnimator
            .ofInt(mSkyView, "backgroundColor", mBlueSkyColor, mSunsetSkyColor)
            .setDuration(3000)
            .apply {
                setEvaluator(ArgbEvaluator())
            }

        //sunsetSkyAnimator.start()

        val nightSkyAnimator = ObjectAnimator
            .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mNightSkyColor)
            .setDuration(1500)
            .apply {
                setEvaluator(ArgbEvaluator())
            }

        val animatorSet = AnimatorSet()
        animatorSet.play(heightAnimator)
            .with(sunsetSkyAnimator)
            .before(nightSkyAnimator)

        animatorSet.start()

        flag = true

    }

    private fun reverseAnimation(){
        val sunYStart: Float = mSkyView.height.toFloat()
        val sunYEnd: Float = mSunView.top.toFloat()

        val heightAnimator: ObjectAnimator = ObjectAnimator
            .ofFloat(mSunView, "y", sunYStart, sunYEnd)
            .setDuration(3000)
            .apply {
                interpolator = AccelerateInterpolator()
            }

        val sunsetSkyAnimator = ObjectAnimator
            .ofInt(mSkyView, "backgroundColor", mBlueSkyColor, mSunsetSkyColor)
            .setDuration(3000)
            .apply {
                setEvaluator(ArgbEvaluator())
            }

        val nightSkyAnimator = ObjectAnimator
            .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mBlueSkyColor)
            .setDuration(1500)
            .apply {
                setEvaluator(ArgbEvaluator())
            }

        val animatorSet = AnimatorSet()
        animatorSet.play(heightAnimator)
            .with(sunsetSkyAnimator)
            .before(nightSkyAnimator)

        animatorSet.start()

        flag = false

    }


}

