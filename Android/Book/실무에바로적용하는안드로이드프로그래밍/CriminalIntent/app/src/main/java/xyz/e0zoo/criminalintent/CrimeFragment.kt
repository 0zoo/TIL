package xyz.e0zoo.criminalintent

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.ShareCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_crime.*
import kotlinx.android.synthetic.main.fragment_crime.view.*
import kotlinx.android.synthetic.main.view_camera_and_title.view.*
import java.io.File
import java.util.*

class CrimeFragment : Fragment() {
    private lateinit var mCrime: Crime
    private lateinit var mDateButton: Button
    private lateinit var mPhotoView: ImageView

    companion object {
        const val ARG_CRIME_ID = "crime_id"
        const val DIALOG_DATE = "DialogDate"
        const val REQUEST_DATE = 0
        const val REQUEST_CONTACT = 1
        const val REQUEST_PHOTO = 2
        const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 99

        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle()
            args.putSerializable(ARG_CRIME_ID, crimeId)
            val crimeFragment = CrimeFragment()
            crimeFragment.arguments = args
            return crimeFragment
        }

    }

    private var number: String? = null
    private var id: String? = null

    private val mPhotoFile: File? by lazy {
        CrimeLab.get(requireActivity()).getPhotoFile(mCrime)
    }

    private var mCallbacks: Callbacks? = null

    private fun updateCrime(){
        CrimeLab.get(requireActivity()).updateCrime(mCrime)
        mCallbacks?.onCrimeUpdated(mCrime)
    }

    interface Callbacks{
        fun onCrimeUpdated(crime: Crime)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mCallbacks = activity as Callbacks
        Log.i("CrimeFragment", "onAttach() call")
    }

    override fun onDetach() {
        super.onDetach()
        mCallbacks = null
        Log.i("CrimeFragment", "onDetach() call")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return

        if (requestCode == REQUEST_DATE) {

            val date = data?.getSerializableExtra(DatePickerFragment.EXTRA_DATE) as Date
            mCrime.date = date
            updateDate()
            updateCrime()

        } else if (requestCode == REQUEST_CONTACT && data != null) {

            val contactUri = data.data
            val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID)

            val c = requireActivity().contentResolver
                    .query(contactUri, queryFields, null, null, null)

            c.use {
                if (c.count == 0) return

                c.moveToFirst()

                val suspect = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID))

                updateCrime()
                mCrime.suspect = suspect
                suspectButton.text = suspect
            }

            if (!id.isNullOrBlank()) askForContactPermission()

        } else if (requestCode == REQUEST_PHOTO) {
            updatePhotoView()
            updateCrime()
        }
    }

    private fun askForContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS)
                        , MY_PERMISSIONS_REQUEST_READ_CONTACTS)

            } else {
                getPhoneNumber()
            }
        } else {
            getPhoneNumber()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPhoneNumber()
                } else {
                    Toast.makeText(requireContext(), "No Permissions ", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun getPhoneNumber() {
        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val fields = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val cursor = requireActivity().contentResolver.query(phoneUri,
                fields,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                arrayOf(id),
                null)

        cursor.use {
            if (it.count == 0) return
            it.moveToFirst()
            number = it.getString(0)
            suspectDialButton.visibility = View.VISIBLE
            suspectDialButton.text = number
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_crime, container, false)

        with(v) {

            titleEditText.setText(mCrime.title)

            titleEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    mCrime.title = s.toString()
                    updateCrime()
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
                updateCrime()
            }

            reportButton.setOnClickListener {
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

            suspectDialButton.visibility = if (number.isNullOrBlank()) View.INVISIBLE else View.VISIBLE

            suspectDialButton.setOnClickListener {
                val phoneNumber = Uri.parse("tel:$number")
                val intent = Intent(Intent.ACTION_DIAL, phoneNumber)
                startActivity(intent)
            }

            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(context.packageManager) != null

            cameraButton.isEnabled = canTakePhoto

            if (canTakePhoto) {
                //val uri = Uri.fromFile(mPhotoFile)
                val photoUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", mPhotoFile!!)
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                //startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)

            }

            cameraButton.setOnClickListener {
                startActivityForResult(captureImage, REQUEST_PHOTO)
            }

            mPhotoView = photoImageView
            updatePhotoView()

        }

        return v
    }

    private fun updatePhotoView() {

        val bitmap = mPhotoFile?.let { file ->
            if (!file.exists())
                null
            else
                PictureUtils.getScaleBitmap(file.path, requireActivity())
        }

        if (bitmap == null)
            mPhotoView.setImageDrawable(null)
        else
            mPhotoView.setImageBitmap(bitmap)

    }


    private fun updateDate() {
        mDateButton.text = mCrime.date.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        mCrime = CrimeLab.get(requireActivity()).getCrime(crimeId)!!
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