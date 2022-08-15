package com.kush.messages_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getStringExtra("name")
        val receiverUid  = intent.getStringExtra("uid")
        val senderUid  = FirebaseAuth.getInstance().currentUser?.uid

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = name

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sendButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        // adding message to recyclerView
        FirebaseDatabase.getInstance().getReference().child("chats")
            .child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {

                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        // adding the message to database
        sendButton.setOnClickListener {
            when {
                TextUtils.isEmpty(messageBox.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@ChatActivity,
                        "Type your message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val message = messageBox.text.toString()
                    val messageObject = Message(message, senderUid)

                    FirebaseDatabase.getInstance().getReference().child("chats")
                        .child(senderRoom!!).child("messages").push().setValue(messageObject)
                        .addOnSuccessListener {
                            FirebaseDatabase.getInstance().getReference().child("chats")
                                .child(receiverRoom!!).child("messages").push()
                                .setValue(messageObject)

                        }
                    messageBox.setText("")
                }
            }
        }
    }
}