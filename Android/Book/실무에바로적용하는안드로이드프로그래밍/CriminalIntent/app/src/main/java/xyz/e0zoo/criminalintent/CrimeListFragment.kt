package xyz.e0zoo.criminalintent

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_crime_list.view.*
import kotlinx.android.synthetic.main.list_item_crime.view.*

class CrimeListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        with(view) {
            crimeRecyclerView.layoutManager = LinearLayoutManager(activity)
            crimeRecyclerView.adapter = CrimeAdapter(CrimeLab.getCrimes())
        }

        return view
    }


    private class CrimeHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private lateinit var crime: Crime

        init {
            itemView.setOnClickListener(this)
        }

        fun bindCrime(crime: Crime) {
            this.crime = crime

            with(itemView) {
                list_item_crime_title_text_view.text = crime.title
                list_item_crime_date_text_view.text = crime.date.toString()
                list_item_crime_solved_check_box.isChecked = crime.solved
            }
        }

        override fun onClick(v: View) {
            Toast.makeText(v.context, "${crime.title} 선택됨!", Toast.LENGTH_SHORT).show()
        }
    }

    private class CrimeAdapter(val crimes: List<Crime>) : RecyclerView.Adapter<CrimeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

        override fun getItemCount(): Int = crimes.size

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) = holder.bindCrime(crimes[position])

    }

}
