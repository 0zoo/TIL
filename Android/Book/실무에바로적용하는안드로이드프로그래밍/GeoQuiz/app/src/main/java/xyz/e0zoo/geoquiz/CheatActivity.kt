package xyz.e0zoo.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_cheat.*

class CheatActivity : AppCompatActivity() {

    companion object {
        private val PACKAGE_NAME = CheatActivity::class.java.`package`.name
        private val EXTRA_ANSWER_IS_TRUE = "$PACKAGE_NAME.answer_is_true"
        private val EXTRA_ANSWER_SHOWN = "$PACKAGE_NAME.answer_shown"

        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent{
            val i = Intent(packageContext, CheatActivity::class.java)
            i.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            return i
        }

        fun wasAnswerShown(result: Intent): Boolean = result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        val mAnswerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE,false)

        showAnswerButton.setOnClickListener {
            if (mAnswerIsTrue)
                answerTextView.setText(R.string.true_button)
            else
                answerTextView.setText(R.string.false_button)

            setAnswerShownResult(true)
        }
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean){
        val data = Intent()
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        setResult(Activity.RESULT_OK, data)
    }

}
