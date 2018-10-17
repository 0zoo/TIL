package xyz.e0zoo.criminalintent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.activity_crime_pager.*
import java.util.*

class CrimePagerActivity : FragmentActivity() {

    companion object {
        private val EXTRA_CRIME_ID = "${CrimePagerActivity::class.java.`package`.name}.crime_id"

        fun newIntent(packageContext: Context, crimeId: UUID): Intent {
            val intent = Intent(packageContext, CrimePagerActivity::class.java)
            intent.putExtra(EXTRA_CRIME_ID, crimeId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime_pager)

        val crimeId = intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID

        val crimes  = CrimeLab.getCrimes()

        crimeViewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment
                    = CrimeFragment.newInstance(crimes[position].id)

            override fun getCount(): Int = crimes.size
        }

        for(index in 0..crimes.size){
            if(crimes[index].id == crimeId){
                crimeViewPager.currentItem = index
                break
            }
        }

    }
}

