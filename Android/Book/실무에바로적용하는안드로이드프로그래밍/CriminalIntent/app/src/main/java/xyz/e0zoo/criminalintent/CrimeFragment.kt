package xyz.e0zoo.criminalintent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.fragment_crime.view.*
import java.util.*

class CrimeFragment : Fragment() {
    private lateinit var mCrime: Crime
    private lateinit var mDateButton: Button

    companion object {
        private const val ARG_CRIME_ID = "crime_id"
        private const val DIALOG_DATE = "DialogDate"
        private const val REQUEST_DATE = 0

        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle()
            args.putSerializable(ARG_CRIME_ID, crimeId)
            val crimeFragment = CrimeFragment()
            crimeFragment.arguments = args
            return crimeFragment
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode != Activity.RESULT_OK) return

        if(requestCode == REQUEST_DATE){
            val date = data?.getSerializableExtra(DatePickerFragment.EXTRA_DATE) as Date
            mCrime.date = date
            updateDate()
        }
    }


    private fun updateDate() {
        mDateButton.text = mCrime.date.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        mCrime = CrimeLab.getCrime(crimeId)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_crime, container, false)

        with(v) {

            crimeTitle.setText(mCrime.title)

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

            mDateButton = crimeDate

            updateDate()

            crimeDate.setOnClickListener {

                DatePickerFragment.newInstance(mCrime.date).let {
                    it.setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                    it.show(fragmentManager, DIALOG_DATE)
                }

            }

            crimeSolved.isChecked = mCrime.solved

            crimeSolved.setOnCheckedChangeListener { _, isChecked ->
                mCrime.solved = isChecked
            }
        }

        return v
    }
}