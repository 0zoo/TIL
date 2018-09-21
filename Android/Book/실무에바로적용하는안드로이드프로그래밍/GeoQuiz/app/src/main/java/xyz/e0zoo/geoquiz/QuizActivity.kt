package xyz.e0zoo.geoquiz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_quiz.*
import org.jetbrains.anko.textResource

class QuizActivity : AppCompatActivity() {
    companion object {
        private val TAG = QuizActivity::class.java.simpleName
        private const val KEY_INDEX = "index"
        private const val REQUEST_CODE_CHEAT = 0
        private const val IS_CHEATER = "IS_CHEATER"
    }

    private val mQuestionBank = listOf(
            Question(R.string.question_oceans, true),
            Question(R.string.question_mideast, false),
            Question(R.string.question_africa, false),
            Question(R.string.question_americas, true),
            Question(R.string.question_asia, true))

    private var mCurrentIndex = 0

    private var mIsCheater: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "OnCreate() called")


        savedInstanceState?.let {
            mCurrentIndex = it.getInt(KEY_INDEX, 0)
            mIsCheater = it.getBoolean(IS_CHEATER, false)
        }

        setContentView(R.layout.activity_quiz)

        trueButton.setOnClickListener {
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            mIsCheater = false
            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.size
            updateQuestion()
        }

        cheatButton.setOnClickListener {
            val answerIsTrue = mQuestionBank[mCurrentIndex].answerTrue
            val i = CheatActivity.newIntent(this, answerIsTrue)
            startActivityForResult(i, REQUEST_CODE_CHEAT)
        }

        updateQuestion()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return

        if (requestCode == REQUEST_CODE_CHEAT) {
            data?.let {
                mIsCheater = CheatActivity.wasAnswerShown(it)
            }
        }
    }

    private fun updateQuestion() {
        questionTextView.textResource = mQuestionBank[mCurrentIndex].textResId
        if (mCurrentIndex == mQuestionBank.size - 1) nextButton.visibility = View.GONE
    }

    private fun checkAnswer(userPressedTrue: Boolean) {

        val messageResId: Int =
                if (mIsCheater) {
                    R.string.judgment_toast
                } else {
                    if (userPressedTrue == mQuestionBank[mCurrentIndex].answerTrue)
                        R.string.correct_toast
                    else
                        R.string.incorrect_toast
                }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState")
        outState?.apply {
            putInt(KEY_INDEX, mCurrentIndex)
            putBoolean(IS_CHEATER, mIsCheater)
        }
    }

}
