package com.example.a0zoo.githubapi.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.a0zoo.githubapi.R
import com.example.a0zoo.githubapi.api.model.GithubRepo
import com.example.a0zoo.githubapi.api.provideGithubApi
import com.example.a0zoo.githubapi.utils.enqueue
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.item_repo.view.*
import org.jetbrains.anko.toast
import com.example.a0zoo.githubapi.utils.GlideApp


// class SearchAdapter : RecyclerView.Adapter<>

// 1. View Holder
class RepoViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_repo, parent, false)
)

// 2. RecyclerView Adapter
class SearchListAdapter : RecyclerView.Adapter<RepoViewHolder>() {
    var items: List<GithubRepo> = emptyList()

    // 재사용 가능한 View가 없을 경우 호출되는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        return RepoViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    // View의 내용을 변경할 때 사용하는 함수
    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val item = items[position]
        // holder.itemView.repoNameText.text = item.fullName
        // holder.itemView.repoOwnerText.text = item.owner.login

        with(holder.itemView) {
            repoNameText.text = item.fullName
            repoOwnerText.text = item.owner.login


            GlideApp.with(this)
                    .load(item.owner.avatarUrl)
                    .placeholder(R.drawable.ic_github_logo)
                    .into(ownerAvatarImage)
        }
    }
}


class SearchActivity : AppCompatActivity() {
    companion object {
        const val TAG = "SearchActivity"
    }

    lateinit var listAdapter: SearchListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //listAdapter = SearchListAdapter()

        searchListView.adapter = SearchListAdapter()
        searchListView.layoutManager = LinearLayoutManager(this)


        val githubApi = provideGithubApi(this)
        val call = githubApi.searchRepository("world")
        call.enqueue({ response ->
            val statusCode = response.code()
            if (statusCode == 200) {
                val result = response.body()
                result?.let {
                    listAdapter.items = it.items
                    listAdapter.notifyDataSetChanged()
                }


            } else {
                toast("error - $statusCode")
            }


        }, { t ->
            toast(t.localizedMessage)
        })
    }
}






