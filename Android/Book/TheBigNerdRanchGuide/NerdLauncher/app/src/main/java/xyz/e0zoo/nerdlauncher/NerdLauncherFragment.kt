package xyz.e0zoo.nerdlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Rect
import android.graphics.drawable.ScaleDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_nerd_launcher.view.*

class NerdLauncherFragment : Fragment() {
    companion object {
        const val TAG = "NerdLauncherFragment"
        fun newInstance(): NerdLauncherFragment = NerdLauncherFragment()
    }

    private lateinit var mRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_nerd_launcher, container, false)

        mRecyclerView = v.recyclerView
        mRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        setupAdapter()

        return v
    }

    private fun setupAdapter() {
        val startupIntent = Intent(Intent.ACTION_MAIN)
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val pm = requireActivity().packageManager

        val activities = pm.queryIntentActivities(startupIntent, 0)

        activities.sortWith(Comparator { p0, p1 ->
            String.CASE_INSENSITIVE_ORDER.compare(p0.loadLabel(pm).toString(), p1.loadLabel(pm).toString())
        })

        Log.i(TAG, "Found ${activities.size} activities.")

        mRecyclerView.adapter = ActivityAdapter(activities)
    }

    inner class ActivityHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private lateinit var mResolveInfo: ResolveInfo
        private val mNameTextView: TextView = itemView as TextView

        init {
            mNameTextView.setOnClickListener {
                val activityInfo = mResolveInfo.activityInfo
                val i = Intent(Intent.ACTION_MAIN)
                    .setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
            }
        }

        fun bindActivity(resolveInfo: ResolveInfo) {
            mResolveInfo = resolveInfo
            val pm = requireActivity().packageManager
            val appName = mResolveInfo.loadLabel(pm).toString()

            val icon = mResolveInfo.loadIcon(pm)

            mNameTextView.apply {
                text = appName
                setCompoundDrawablesWithIntrinsicBounds(icon, null, null,null)
            }

        }
    }

    inner class ActivityAdapter(val activities: List<ResolveInfo>) : RecyclerView.Adapter<ActivityHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
            val layoutInflater = LayoutInflater.from(activity)
            val view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            return ActivityHolder(view)
        }

        override fun getItemCount(): Int = activities.size

        override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
            val resolveInfo = activities[position]
            holder.bindActivity(resolveInfo)
        }
    }
}
