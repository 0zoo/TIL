package xyz.e0zoo.criminalintent

import android.support.v4.app.Fragment
import kotlinx.android.synthetic.main.activity_twopane.*

class CrimeListActivity: SingleFragmentActivity(),
        CrimeListFragment.Callbacks, CrimeFragment.Callbacks{

    override fun onCrimeUpdated(crime: Crime) {
        val listFragment
                = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as CrimeListFragment
        listFragment.updateUI()
    }

    override fun onCrimeSelected(crime: Crime) {
        detailFragmentContainer?.let {
            val newDetail = CrimeFragment.newInstance(crime.id)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.detailFragmentContainer, newDetail)
                    .commit()
        } ?: startActivity(CrimePagerActivity.newIntent(this, crime.id))
    }

    override fun getLayoutResId(): Int = R.layout.activity_masterdetail

    override fun createFragment(): Fragment = CrimeListFragment()
}