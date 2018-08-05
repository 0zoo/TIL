package xyz.youngzz.firebase.chat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.item_chat.view.*
import kotlin.properties.Delegates

data class ChatItem(val name: String, val body: String) {
    fun toJson(): Map<String, Any> {
        return mapOf(
                "name" to name,
                "body" to body
        )
    }
}

// 데이터의 변경을 확인하는 2가지 방법
// 1) pull
// : 데이터가 필요한 사람이 필요한 시점에 끌어온다.
// 2) push - Observer Pattern
// : 데이터가 변경된 시점에 필요한 사람에게 알려준다.

class ChatModel {
    // Realtime Database
    var chatItems: List<ChatItem> by Delegates.observable(emptyList()) { _, _, new ->
        // onChangedChatItems?.invoke(new)
        onChangedChatItems?.let {
            it(new)
        }
    }

    var onChangedChatItems: ((List<ChatItem>) -> Unit)? = null

    private val database = FirebaseDatabase.getInstance() // Firebase Database Instance
    private val chatRef = database.getReference("chat-items")

    // 초기화 블록
    init {
        chatRef.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatItems = snapshot.children.map {
                    val name = it.child("name").value as String
                    val body = it.child("body").value as String
                    ChatItem(name, body)
                }
            }
        })
    }


    fun postChat(item: ChatItem) {
        val newRef = chatRef.push()
        newRef.setValue(item.toJson())
    }
}

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
            bodyTextView.text = item.body
        }
    }
}


// MVC - Activity(Fragment) == Controller
class ChatActivity : AppCompatActivity() {
    private val model = ChatModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val adapter = ChatAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.items = model.chatItems
        model.onChangedChatItems = {
            adapter.items = it
        }

        sendButton.setOnClickListener {
            //model.postChat(ChatItem(name = "Chansik", body = "Hello"))

            FirebaseAuth.getInstance().currentUser?.let{
                val name = it.displayName
                val body = editText.text

                if(body.isNotBlank()){
                    model.postChat(ChatItem(name?:"Unnamed",body.toString()))
                    editText.text.clear()
                    //recyclerView.layoutManager.scrollToPosition(0)
                }
            }
        }
    }
}








