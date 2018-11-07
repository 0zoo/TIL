package xyz.e0zoo.criminalintent

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import kotlinx.android.synthetic.main.fragment_crime_list.view.*
import kotlinx.android.synthetic.main.list_item_crime.view.*

class CrimeListFragment : Fragment() {

    companion object {
        private const val SAVED_SUBTITLE_VISIBLE = "subtitle"
    }
    private var mSubtitleVisible = false
    private lateinit var mCrimeRecyclerView: RecyclerView
    private var mAdapter: CrimeAdapter? = null

    private var mCallbacks: Callbacks? = null

    interface Callbacks{
        fun onCrimeSelected(crime: Crime)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mCallbacks = activity as Callbacks
        Log.i("CrimeListFragment", "onAttach() call")
    }

    override fun onDetach() {
        super.onDetach()
        mCallbacks = null
        Log.i("CrimeListFragment", "onDetach() call")
    }

    fun updateUI() {
        val crimeLab = CrimeLab.get(requireContext())
        val crimes = crimeLab.getCrimes()

        if (mAdapter == null) {
            mAdapter = CrimeAdapter(crimes)
            mCrimeRecyclerView.adapter = mAdapter
        } else {
            mAdapter!!.setCrimes(crimes)
            mAdapter!!.notifyDataSetChanged()
        }
        updateSubtitle()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)

        menu.findItem(R.id.menu_item_show_subtitle).let {
            if(mSubtitleVisible) it.setTitle(R.string.hide_subtitle)
            else it.setTitle(R.string.show_subtitle)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_item_new_crime -> {
                val crime = Crime()
                CrimeLab.get(requireContext()).addCrime(crime)

                //startActivity(CrimePagerActivity.newIntent(requireContext(), crime.id))
                updateUI()
                mCallbacks?.onCrimeSelected(crime)

                true
            }
            R.id.menu_item_show_subtitle -> {
                mSubtitleVisible = !mSubtitleVisible
                requireActivity().invalidateOptionsMenu()
                updateSubtitle()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        mCrimeRecyclerView = view.crimeRecyclerView
        mCrimeRecyclerView.layoutManager = LinearLayoutManager(activity)

        savedInstanceState?.let {
            mSubtitleVisible = it.getBoolean(SAVED_SUBTITLE_VISIBLE)
        }

        updateUI()

        return view
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }


    @SuppressLint("StringFormatMatches")
    private fun updateSubtitle() {
        val crimeCount = CrimeLab.get(requireContext()).getCrimes().size
        val activity = requireActivity() as AppCompatActivity

        val subtitle: String? = if(!mSubtitleVisible) null else getString(R.string.subtitle_format, crimeCount)
        //val subtitle: String? = if(!mSubtitleVisible) null else resources.getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount)

        activity.supportActionBar?.subtitle = subtitle
    }


    inner class CrimeHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private lateinit var crime: Crime

        init {
            itemView.setOnClickListener(this)
        }

        fun bindCrime(crime: Crime) {
            this.crime = crime

            with(itemView) {
                titleTextView.text = crime.title
                dateTextView.text = crime.date.toString()
                solvedCheckBox.isChecked = crime.solved
            }
        }

        override fun onClick(v: View) {
            //val intent = CrimePagerActivity.newIntent(this@CrimeListFragment.requireContext(), crime.id)
            //startActivity(intent)
            mCallbacks?.onCrimeSelected(crime)
        }
    }

    inner class CrimeAdapter(private var crimes: List<Crime>) : RecyclerView.Adapter<CrimeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

        override fun getItemCount(): Int = crimes.size

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) = holder.bindCrime(crimes[position])

        fun setCrimes(crimes: List<Crime>){
            this.crimes = crimes
        }

    }

}
