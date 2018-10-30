package xyz.e0zoo.criminalintent

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.support.v4.app.ShareCompat
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.fragment_crime.*
import kotlinx.android.synthetic.main.fragment_crime.view.*
import java.util.*

class CrimeFragment : Fragment() {
    private lateinit var mCrime: Crime
    private lateinit var mDateButton: Button

    companion object {
        const val ARG_CRIME_ID = "crime_id"
        const val DIALOG_DATE = "DialogDate"
        const val REQUEST_DATE = 0
        const val REQUEST_CONTACT = 1
        const val REQUEST_CONTACT_DIAL = 2

        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle()
            args.putSerializable(ARG_CRIME_ID, crimeId)
            val crimeFragment = CrimeFragment()
            crimeFragment.arguments = args
            return crimeFragment
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return

        if (requestCode == REQUEST_DATE) {
            val date = data?.getSerializableExtra(DatePickerFragment.EXTRA_DATE) as Date
            mCrime.date = date
            updateDate()
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            val contactUri = data.data
            // 값을 반환할 쿼리 필드 지정
            val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
            // 쿼리 수행
            // contactUri는 SQL의 where절
            val c = requireActivity().contentResolver.query(contactUri, queryFields, null, null, null)
            c.use {
                // 쿼리 결과 데이터 존재 여부 재확인.
                if (c.count == 0) return

                // 커서가 하나의 행만 포함하므로
                // 첫번째 행의 첫번째 열 추출
                // -> 용의자 이름
                c.moveToFirst()
                val suspect = c.getString(0)
                mCrime.suspect = suspect
                suspectButton.text = suspect
            }

        }else if (requestCode == REQUEST_CONTACT_DIAL && data != null) {
            val contactUri = data.data
            val queryFields
                    = arrayOf(ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID)
            val cr = requireActivity().contentResolver
            val c = cr.query(contactUri, queryFields, null, null, null)

            c.use {
                if (c.count == 0) return
                c.moveToFirst()
                //val suspect = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                //val suspect = c.getString(0)
                val id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID))

                //val phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, )

            }
        }
    }


    private fun updateDate() {
        mDateButton.text = mCrime.date.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        mCrime = CrimeLab.get(requireActivity()).getCrime(crimeId)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_crime, container, false)

        with(v) {

            crimeTitleEditText.setText(mCrime.title)

            crimeTitleEditText.addTextChangedListener(object : TextWatcher {
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

            mDateButton = dateButton

            updateDate()

            dateButton.setOnClickListener {
                val datePickerFragment = DatePickerFragment.newInstance(mCrime.date)

                datePickerFragment.setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                datePickerFragment.show(fragmentManager, DIALOG_DATE)
            }

            solvedCheckBox.isChecked = mCrime.solved

            solvedCheckBox.setOnCheckedChangeListener { _, isChecked ->
                mCrime.solved = isChecked
            }

            reportButton.setOnClickListener {
                /*
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
                startActivity(Intent.createChooser(i, getString(R.string.send_report)))
                */
                val i = ShareCompat.IntentBuilder.from(requireActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(getString(R.string.send_report))
                        .createChooserIntent()
                startActivity(i)
            }

            val pickContact = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

            suspectButton.setOnClickListener {
                startActivityForResult(pickContact, REQUEST_CONTACT)
            }

            mCrime.suspect?.let {
                suspectButton.text = it
            }

            val packageManager = requireActivity().packageManager
            if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null)
                suspectButton.isEnabled = false


            suspectDialButton.setOnClickListener {
                startActivityForResult(pickContact, REQUEST_CONTACT_DIAL)
            }
        }

        return v
    }

    override fun onPause() {
        super.onPause()
        CrimeLab.get(requireActivity()).updateCrime(mCrime)
    }

    private fun getCrimeReport(): String {

        val solvedString =
                if (mCrime.solved) getString(R.string.crime_report_solved)
                else getString(R.string.crime_report_unsolved)

        val dateFormat = "EEE, MMM dd"
        val dateString = DateFormat.format(dateFormat, mCrime.date).toString()

        val suspect = if (mCrime.suspect == null) getString(R.string.crime_report_no_suspect)
        else getString(R.string.crime_report_suspect, mCrime.suspect)

        return getString(R.string.crime_report, mCrime.title, dateString, solvedString, suspect)

    }
}