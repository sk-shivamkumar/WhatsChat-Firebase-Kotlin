package com.skappstech.chat

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MessageActivity : AppCompatActivity() {
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef : DatabaseReference
    private lateinit var etMessage : EditText
    private lateinit var fabSend : FloatingActionButton
    private lateinit var rvMessage : RecyclerView
    private lateinit var toolbarMsg : androidx.appcompat.widget.Toolbar

    var receiverRoom: String? = null
    var senderRoom: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        etMessage = findViewById(R.id.etMessage)
        fabSend = findViewById(R.id.fabSend)
        rvMessage = findViewById(R.id.rvMessage)
        toolbarMsg = findViewById(R.id.toolbarMsg)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        toolbarMsg.title = name

        mDbRef = FirebaseDatabase.getInstance().getReference()

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        setRecyclerView()
        setListeners()


        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for (postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        fabSend.setOnClickListener {

            val message = etMessage.text.toString()
            val messageObject = Message(message,senderUid)

            if (TextUtils.isEmpty(message)){
                etMessage.error = "Type a message"
            }else {

                mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject)
                mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                    .setValue(messageObject)
                etMessage.setText("")
            }
        }

    }

    private fun setListeners() {
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)

        rvMessage.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        rvMessage.adapter = messageAdapter

        rvMessage.scrollToPosition(messageList.size-1)
    }

    private fun setRecyclerView() {
        rvMessage.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                if (messageList.isEmpty()){
                    rvMessage.postDelayed(Runnable {
                        rvMessage.smoothScrollToPosition(messageList.size) }, 100)
                }else{
                    rvMessage.postDelayed(Runnable {
                        rvMessage.smoothScrollToPosition(messageList.size-1) }, 100)
                }
            }
        }
    }
}