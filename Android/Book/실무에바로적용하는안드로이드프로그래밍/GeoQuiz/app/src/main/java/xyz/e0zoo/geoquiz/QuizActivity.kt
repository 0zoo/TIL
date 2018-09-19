package xyz.e0zoo.geoquiz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_quiz.*

class QuizActivity : AppCompatActivity() {
    companion object {
        private val TAG = QuizActivity::class.java.simpleName
        private const val KEY_INDEX = "index"

    }

    private val mQuestionBank = listOf(
            Question(R.string.question_oceans, true),
            Question(R.string.question_mideast, false),
            Question(R.string.question_africa, false),
            Question(R.string.question_americas, true),
            Question(R.string.question_asia, true))

    private var mCurrentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG,"OnCreate() called")

        savedInstanceState?.let {
            mCurrentIndex = it.getInt(KEY_INDEX,0)
        }

        setContentView(R.layout.activity_quiz)

        trueButton.setOnClickListener {
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            updateQuestion()
        }

        questionTextView.setOnClickListener {
            updateQuestion()
        }

        updateQuestion()

    }

    private fun updateQuestion() {
        //mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.size
        val question: Int = mQuestionBank[mCurrentIndex].textResId
        questionTextView.setText(question)
    }

    private fun checkAnswer(userPressedTrue: Boolean) {

        val messageResId: Int =
                if (userPressedTrue == mQuestionBank[mCurrentIndex].answerTrue)
                    R.string.correct_toast
                else
                    R.string.incorrect_toast


        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

    }



    override fun onStart() {
        super.onStart()
        Log.d(TAG,"onStart() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG,"onPause() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"onResume() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG,"onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"onDestroy() called")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState")
        outState?.let {
            it.putInt(KEY_INDEX,mCurrentIndex)
        }
    }

}
