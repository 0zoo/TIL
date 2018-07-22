package com.example.a0zoo.githubapi.ui

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.a0zoo.githubapi.R
import com.example.a0zoo.githubapi.api.model.GithubRepo
import com.example.a0zoo.githubapi.api.provideGithubApi
import com.example.a0zoo.githubapi.utils.enqueue
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {
    companion object {
        const val TAG = "SearchActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val githubApi = provideGithubApi(this)
        val call = githubApi.searchRepository("kotlin")

        call.enqueue({ response ->

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.i(TAG, it.toString())
                }

            } else {
                Log.i(TAG, "response code: ${response.code()}")
            }

        }, {
            Log.e(TAG, it.localizedMessage)
        })


    }


}









