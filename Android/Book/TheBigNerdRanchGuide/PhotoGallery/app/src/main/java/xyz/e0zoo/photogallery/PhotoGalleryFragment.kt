package xyz.e0zoo.photogallery

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_photo_gallery.view.*
import java.lang.ref.WeakReference

class PhotoGalleryFragment : Fragment() {

    private lateinit var mPhotoRecyclerView: RecyclerView
    private var mItems: List<GalleryItem> = arrayListOf()
    private lateinit var mThumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    companion object {
        const val TAG = "PhotoGalleryFragment"

        fun newInstance(): PhotoGalleryFragment = PhotoGalleryFragment()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        FetchItemsTask(this).execute()

        mThumbnailDownloader = ThumbnailDownloader()
        mThumbnailDownloader.start()
        mThumbnailDownloader.looper
        Log.i(TAG, "Background Thread started.")
    }

    override fun onDestroy() {
        super.onDestroy()
        mThumbnailDownloader.quit()
        Log.i(TAG, "Background Thread destroyed.")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
        mPhotoRecyclerView = v.photoRecyclerView
        mPhotoRecyclerView.layoutManager = GridLayoutManager(requireActivity(), 3)
        setupAdapter()
        return v
    }

    fun setupAdapter() {
        if (isAdded) {
            mPhotoRecyclerView.adapter = PhotoAdapter(mItems)
        }
    }

    private class FetchItemsTask internal constructor(context: PhotoGalleryFragment) :
        AsyncTask<Void, Void, List<GalleryItem>>() {

        private val reference: WeakReference<PhotoGalleryFragment> = WeakReference(context)

        override fun doInBackground(vararg params: Void?): List<GalleryItem>? {
            /*
            try {
                val result = FlickrFetchr().getUrlString("https://m.naver.com")
                Log.i(TAG, "Fetched contents of URL $result")
            } catch (e: IOException) {
                Log.e(TAG, "Failed to fetch URL ", e)
            }
            */
            return FlickrFetchr().fetchItems()
        }

        override fun onPostExecute(result: List<GalleryItem>?) {
            reference.get()?.let {
                if (it.isRemoving) return
                it.mItems = result ?: return
                it.setupAdapter()
            }
        }
    }

    inner class PhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageView = itemView.findViewById(R.id.imageView) as ImageView
        fun bindGalleryItem(item: GalleryItem) {
            GlideApp.with(itemView)
                .load(item.url)
                .into(mImageView)

        }
    }

    inner class PhotoAdapter(val items: List<GalleryItem>) : RecyclerView.Adapter<PhotoHolder>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PhotoHolder {
            val inflater = LayoutInflater.from(viewGroup.context)
            val view = inflater.inflate(R.layout.gallery_item, viewGroup, false)
            return PhotoHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(photoHolder: PhotoHolder, position: Int) {
            val item = items[position]
            photoHolder.bindGalleryItem(item)
            mThumbnailDownloader.queueThumbnail(photoHolder, item.url)
        }
    }


}
