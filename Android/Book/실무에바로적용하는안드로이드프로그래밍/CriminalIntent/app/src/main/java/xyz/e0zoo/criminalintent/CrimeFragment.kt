package xyz.e0zoo.criminalintent

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_crime.view.*

class CrimeFragment : Fragment() {
    private lateinit var mCrime: Crime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCrime = Crime()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_crime, container, false)

        with(v) {

            crimeTitle.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // 이 메서드의 실행 코드는 여기서는 필요 없음.
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // 이 메서드의 실행 코드는 여기서는 필요 없음.
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    mCrime.title = s.toString()
                }
            })

            crimeDate.text = mCrime.date.toString()
            crimeDate.isEnabled = false

            crimeSolved.setOnCheckedChangeListener { _, isChecked ->
                mCrime.solved = isChecked }

        }

        return v
    }
}