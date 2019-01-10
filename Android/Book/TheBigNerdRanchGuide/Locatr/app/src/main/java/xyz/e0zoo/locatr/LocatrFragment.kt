package xyz.e0zoo.locatr


import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.ImageView
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import java.io.IOException

class LocatrFragment : Fragment() {

    private lateinit var mImageView: ImageView

    private val mClient: GoogleApiClient by lazy {
        GoogleApiClient.Builder(requireContext())
            .addApi(LocationServices.API)
            .addConnectionCallbacks(object: GoogleApiClient.ConnectionCallbacks{
                override fun onConnected(p0: Bundle?) {
                    requireActivity().invalidateOptionsMenu()
                }

                override fun onConnectionSuspended(p0: Int) {
                }

            })
            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_locatr, container, false)
        mImageView = v.findViewById(R.id.image)

        return v
    }

    companion object {
        const val TAG: String = "LocatrFragment"

        @JvmStatic
        fun newInstance() = LocatrFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_locatr, menu)

        val searchItem = menu.findItem(R.id.action_locate)
        searchItem.isEnabled = mClient.isConnected
    }

    override fun onStart() {
        super.onStart()
        requireActivity().invalidateOptionsMenu()
        mClient.connect()
    }

    override fun onStop() {
        super.onStop()
        mClient.disconnect()
    }

    @SuppressLint("MissingPermission")
    private fun findImage() {
        val request = LocationRequest.create()
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        request.numUpdates = 1
        request.interval = 0

        LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, object: LocationListener{
            override fun onLocationChanged(location: Location?) {
                Log.i(TAG, "Got a fix: $location")
                SearchTask().execute(location)
            }
        })

       //getFusedLocationProviderClient(requireContext()).requestLocationUpdates(request,LocationCallback(){})

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.itemId.let{
            if(it == R.id.action_locate){
                findImage()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class SearchTask: AsyncTask<Location, Void, Void>() {

        private lateinit var mGalleryItem: GalleryItem
        private lateinit var mBitmap: Bitmap

        override fun doInBackground(vararg params: Location): Void? {

            val fetchr = FlickrFetchr()
            val items = fetchr.searchPhotos(params[0])

            if(items.isEmpty()) return null

            mGalleryItem = items[0]

            try {
                mGalleryItem.url?.let { url ->
                    val bytes = fetchr.getUrlBytes(url)
                    mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
            }catch (ioe: IOException){

            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            mImageView.setImageBitmap(mBitmap)
        }

    }
}













