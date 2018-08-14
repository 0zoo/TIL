package xyz.youngzz.firebase.chat.controller

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.item_chat.view.*
import xyz.youngzz.firebase.chat.R
import xyz.youngzz.firebase.chat.model.ChatModel
import java.io.ByteArrayOutputStream
import kotlin.properties.Delegates

class ChatItem private constructor(val name: String, val body: String?, val image: String?) {
    companion object {
        fun chat(name: String, body: String): ChatItem {
            return ChatItem(name, body, null)
        }

        fun image(name: String, image: String): ChatItem {
            return ChatItem(name, null, image)
        }
    }

    fun toJson(): Map<String, Any> {
        val json = mutableMapOf<String, Any>(
                "name" to name
        )

        body?.let {
            json.put("body", body)
        }

        image?.let {
            json.put("image", image)
        }

        return json
    }
}

// 데이터의 변경을 확인하는 2가지 방법
// 1) pull
//   : 데이터가 필요한 사람이 필요한 시점에 끌어온다.
// 2) push - Observer Pattern
//   : 데이터가 변경된 시점에 필요한 사람에게 알려준다.


class ChatViewHolder(parent: ViewGroup)
    : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false))

class ChatAdapter : RecyclerView.Adapter<ChatViewHolder>() {
    var items: List<ChatItem> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChatViewHolder(parent)
    override fun getItemCount() = items.count()
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = items[position]

        // holder.itemView.nameTextView.text = item.name
        // holder.itemView.bodyTextView.text = item.body
        with(holder.itemView) {
            nameTextView.text = item.name

            item.body?.let {
                bodyTextView.text = item.body
                bodyTextView.visibility = View.VISIBLE
                imageView.visibility = View.GONE
            }

            item.image?.let {
                bodyTextView.visibility = View.GONE
                imageView.visibility = View.VISIBLE
                GlideApp.with(this)
                        .load(item.image)
                        .centerCrop()
                        .into(imageView)
            }


        }
    }
}


// MVC - Activity(Fragment) == Controller
class ChatActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1000
    }

    private val model = ChatModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val adapter = ChatAdapter()
        val layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        adapter.items = model.chatItems
        model.onChangedChatItems = {
            adapter.items = it
            // recyclerView.scrollToPosition(recyclerView.adapter.itemCount - 1)
            if (it.isNotEmpty())
            //recyclerView.smoothScrollToPosition(recyclerView.adapter.itemCount - 1)
                recyclerView.betterSmoothScrollToPosition(recyclerView.adapter.itemCount - 1)
        }

        sendButton.setOnClickListener { _ ->
            // model.postChat(ChatItem(name = "Chansik", body = "Hello"))
            FirebaseAuth.getInstance().currentUser?.let {
                val name = it.displayName
                val body = editText.text

                if (body.isNotBlank()) {
                    model.postChat(ChatItem.chat(name ?: "Unnamed", body.toString()))
                    editText.text.clear()
                }
            }
        }

        cameraButton.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // val extras: Bundle = data?.extras!! // !
            // val imageBitmap = extras.get("data") as Bitmap
            // imageBitmap

            data?.extras?.let { extras ->
                (extras.get("data") as? Bitmap)?.let { bitmap ->

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    model.postImage(baos.toByteArray()) { imageUrl ->

                        FirebaseAuth.getInstance().currentUser?.let {
                            it.displayName?.let { displayName ->
                                model.postChat(ChatItem.image(displayName, imageUrl))
                            }
                        }
                    }
                }
            }
        }
    }
}


fun RecyclerView.betterSmoothScrollToPosition(targetItem: Int) {
    layoutManager?.apply {
        val maxScroll = 20
        when (this) {
            is LinearLayoutManager -> {
                val topItem = findFirstVisibleItemPosition()
                val distance = topItem - targetItem
                val anchorItem = when {
                    distance > maxScroll -> targetItem + maxScroll
                    distance < -maxScroll -> targetItem - maxScroll
                    else -> topItem
                }
                if (anchorItem != topItem) scrollToPosition(anchorItem)
                post {
                    smoothScrollToPosition(targetItem)
                }
            }
            else -> smoothScrollToPosition(targetItem)
        }
    }
}

@GlideModule
class ChatAppGlideModule : AppGlideModule()

