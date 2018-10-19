package xyz.e0zoo.criminalintent

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.dialog_date.view.*
import java.util.*

class DatePickerFragment : DialogFragment() {

    companion object {
        private const val ARG_DATE = "date"

        val EXTRA_DATE = "${DatePickerFragment::class.java.`package`.name}.date"

        fun newInstance(date: Date): DatePickerFragment {
            val args = Bundle()
            args.putSerializable(ARG_DATE, date)
            val fragment = DatePickerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun sendResult(resultCode: Int, date: Date) {
        targetFragment?.let { target ->
            val intent = Intent()
            intent.putExtra(EXTRA_DATE, date)
            target.onActivityResult(targetRequestCode, resultCode, intent)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog{

        val calendar = Calendar.getInstance()
        calendar.time = arguments?.getSerializable(ARG_DATE) as Date

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val v = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_date, null)
        v.dialogDatePicker.init(year, month, day, null)

        return AlertDialog.Builder(requireActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                //.setPositiveButton(android.R.string.ok, null)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    with(v.dialogDatePicker) {
                        sendResult(Activity.RESULT_OK, GregorianCalendar(this.year, this.month, this.dayOfMonth).time)
                    }
                }
                .create()
    }

}