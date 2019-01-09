package xyz.e0zoo.locatr

import android.support.v4.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class LocatrActivity : SingleFragmentActivity() {

    companion object {
        const val REQUEST_ERROR: Int = 0
    }

    override fun createFragment(): Fragment = LocatrFragment.newInstance()

    override fun onResume() {
        super.onResume()

        val errorCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)

        if(errorCode != ConnectionResult.SUCCESS){
            val errorDialog = GoogleApiAvailability.getInstance()
                .getErrorDialog(this, errorCode, REQUEST_ERROR) {
                    // 서비스를 사용할 수 없으면 실행을 중단한다.
                    finish()
                }
            errorDialog.show()
        }
    }

}
