package xyz.e0zoo.photogallery

import android.net.Uri
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class FlickrFetchr {

    companion object {
        const val TAG = "FlickrFetchr"
        const val API_KEY = "765f4dc25b9de6ec574e342e097c6e97"
    }

    @Throws(IOException::class)
    private fun getUrlBytes(urlSpec: String): ByteArray {
        val url = URL(urlSpec)
        val connection = url.openConnection() as HttpURLConnection

        try {

            val out = ByteArrayOutputStream()
            val cin = connection.inputStream


            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("${connection.responseMessage}: with $urlSpec")
            }

            cin.copyTo(out, 1024)

            out.close()

            return out.toByteArray()

        } finally {
            connection.disconnect()
        }
    }

    @Throws(IOException::class)
    fun getUrlString(urlSpec: String): String = String(getUrlBytes(urlSpec))

    fun fetchItems(): List<GalleryItem> {
        val items = ArrayList<GalleryItem>()

        try {
            val url = Uri.parse("https://api.flickr.com/services/rest/")
                .buildUpon()
                .appendQueryParameter("method", "flickr.photos.getRecent")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .appendQueryParameter("extras", "url_s")
                .build().toString()

            val jsonString = getUrlString(url)
            Log.i(TAG, "Received JSON: $jsonString");
            val jsonBody = JSONObject(jsonString)
            parseItems(items, jsonBody)

        } catch (je: JSONException) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (ioe: IOException) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        return items
    }

    @Throws(IOException::class, JSONException::class)
    private fun parseItems(items: ArrayList<GalleryItem>, jsonBody: JSONObject) {
        val photosJsonObject = jsonBody.getJSONObject("photos")
        val photoJsonArray = photosJsonObject.getJSONArray("photo")

        for (i in 0..photoJsonArray.length()) {
            val photoJsonObject = photoJsonArray.getJSONObject(i)
            val item = GalleryItem()
            item.id = photoJsonObject.getString("id")
            item.caption = photoJsonObject.getString("title")

            if (!photoJsonObject.has("url_s")) {
                continue
            }
            item.url = photoJsonObject.getString("url_s")
            items.add(item)
        }
    }
}




