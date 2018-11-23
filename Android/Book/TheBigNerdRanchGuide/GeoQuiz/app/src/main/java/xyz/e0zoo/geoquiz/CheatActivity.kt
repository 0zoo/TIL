package xyz.e0zoo.geoquiz

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import kotlinx.android.synthetic.main.activity_cheat.*
import org.jetbrains.anko.textResource

class CheatActivity : AppCompatActivity() {

    companion object {
        private val PACKAGE_NAME = CheatActivity::class.java.`package`.name
        private val EXTRA_ANSWER_IS_TRUE = "$PACKAGE_NAME.answer_is_true"
        private val EXTRA_ANSWER_SHOWN = "$PACKAGE_NAME.answer_shown"

        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            val i = Intent(packageContext, CheatActivity::class.java)
            i.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            return i
        }

        fun wasAnswerShown(result: Intent): Boolean = result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false)
    }

    private var isAnswerShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        val answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        savedInstanceState?.let {
            isAnswerShown = it.getBoolean(EXTRA_ANSWER_SHOWN, false)
            if (isAnswerShown) setAnswerTextView(answerIsTrue)
        }

        showAnswerButton.setOnClickListener {
            isAnswerShown = true
            setAnswerTextView(answerIsTrue)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val cx = showAnswerButton.width / 2
                val cy = showAnswerButton.height / 2
                val radius = showAnswerButton.width.toFloat()

                val anim = ViewAnimationUtils.createCircularReveal(showAnswerButton, cx, cy, radius, 0f)

                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        // 애니메이션이 끝나면 정답을 보여주고 정답 보기 버튼을 감춘다.
                        showAnswerButton.visibility = View.INVISIBLE
                    }
                })

                anim.start()
            } else {
                showAnswerButton.visibility = View.INVISIBLE
            }
        }

        val apiLevel = "API 레벨 ${Build.VERSION.SDK_INT}"
        apiLevelTextView.text = apiLevel
    }

    private fun setAnswerTextView(mAnswerIsTrue: Boolean) {
        if (mAnswerIsTrue)
            answerTextView.textResource = R.string.true_button
        else
            answerTextView.textResource = R.string.false_button

        setAnswerShownResult(true)
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent()
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        setResult(Activity.RESULT_OK, data)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(EXTRA_ANSWER_SHOWN, isAnswerShown)
    }

}
