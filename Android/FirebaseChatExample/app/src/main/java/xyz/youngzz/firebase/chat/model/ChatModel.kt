package xyz.youngzz.firebase.chat.model

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import xyz.youngzz.firebase.chat.controller.ChatItem
import kotlin.properties.Delegates

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
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatItems = snapshot.children.map {
                    val name = it.child("name").value as String
                    val body = it.child("body").value as? String
                    val image = it.child("image").value as? String

                    if (body != null) {
                        ChatItem.chat(name, body)
                    } else {
                        ChatItem.image(name, image!!)
                    }
                }
            }
        })
    }


    fun postChat(item: ChatItem) {
        val newRef = chatRef.push()
        newRef.setValue(item.toJson())
    }

    // bitmap을 인자로 바로 보내지 않는 이유?
    // -> 종속성이 생기기 때문.
    // bitmap은 안드로이드 라이브러리를 사용하기 때문에 독립적으로 테스트 불가.
    // Task: Thread를 추상화한 개념
    fun postImage(data: ByteArray,
                  onFailure: ((Exception) -> Unit)? = null,
                  onSuccess: ((String) -> Unit)? = null) {
        // Log.d("ChatModel", generateImageName())

        val storage = FirebaseStorage.getInstance()
        val imagesRef = storage.getReference("images")
        val uploadRef = imagesRef.child(generateImageName())

        val uploadTask = uploadRef.putBytes(data)
        with(uploadTask) {
            addOnFailureListener {
                onFailure?.invoke(it)
            }

            addOnSuccessListener {
                it?.metadata?.reference?.downloadUrl?.let { task ->
                    task.addOnSuccessListener { uri ->
                        // Log.d("UploadTask", uri.toString())
                        onSuccess?.invoke(uri.toString())
                    }
                }
            }
        }

    }
    // Url을 요청하는 것도 비동기적으로 처리해야 한다.

    // firebase_chat_18_08_11_16_26_30.jpeg
    private fun generateImageName(): String {
        // val now = LocalDateTime.now() - API 26, Java 8
        val now = LocalDateTime.now()
        val formatter = DateTimeFormat.forPattern("yy_MM_dd_HH_mm_ss")

        return "firebase_chat_${now.toString(formatter)}.jpeg"
    }


}



















