package xyz.e0zoo.criminalintent

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
                startActivity(CrimePagerActivity.newIntent(requireContext(), crime.id))
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

    private fun updateUI() {
        if (mAdapter == null) {
            mAdapter = CrimeAdapter(CrimeLab.get(requireContext()).getCrimes())
            mCrimeRecyclerView.adapter = mAdapter
        } else {
            mAdapter!!.notifyDataSetChanged()
        }
        updateSubtitle()
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
                list_item_crime_title_text_view.text = crime.title
                list_item_crime_date_text_view.text = crime.date.toString()
                list_item_crime_solved_check_box.isChecked = crime.solved
            }
        }

        override fun onClick(v: View) {
            val intent = CrimePagerActivity.newIntent(this@CrimeListFragment.requireContext(), crime.id)
            startActivity(intent)
        }
    }

    inner class CrimeAdapter(private val crimes: List<Crime>) : RecyclerView.Adapter<CrimeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

        override fun getItemCount(): Int = crimes.size

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) = holder.bindCrime(crimes[position])

    }


}
