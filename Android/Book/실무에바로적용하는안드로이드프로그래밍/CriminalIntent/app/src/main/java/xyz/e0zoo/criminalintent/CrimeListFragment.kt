package xyz.e0zoo.criminalintent

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_crime_list.view.*

class CrimeListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        view.crimeRecyclerView.layoutManager = LinearLayoutManager(activity)

        return view
    }

    private class CrimeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            val mTitleTextView: TextView = itemView as TextView
        }
    }
}
