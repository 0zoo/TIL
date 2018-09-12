package xyz.e0zoo.geoquiz

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_quiz.*

class QuizActivity : AppCompatActivity() {

    private val mQuestionBank = listOf(
            Question(R.string.question_oceans, true),
            Question(R.string.question_mideast, false),
            Question(R.string.question_africa, false),
            Question(R.string.question_americas, true),
            Question(R.string.question_asia, true))

    private var mCurrentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.size
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
}
