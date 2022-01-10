package com.skappstech.chat.mainFragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skappstech.chat.R
import com.skappstech.chat.adapters.ChatsAdapter
import com.skappstech.chat.adapters.User

class Chats : Fragment() {

    private lateinit var chatsAdapter: ChatsAdapter
    private val contactInfo = arrayListOf<User>()
    private lateinit var fstore : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private lateinit var rvContact : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        rvContact = view.findViewById(R.id.rvContact)

        fstore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        fstore.collection("users").get().addOnSuccessListener {
            if (!it.isEmpty){
                val listContact = it.documents
                for(i in listContact){
                    if (i.id== auth.currentUser?.uid){
                        Log.d("onFound","This is User Account")
                    }else{
                        val contact = User(i.getString("userName").toString(),
                            i.getString("userEmail").toString(),
                            i.getString("userStatus").toString(),
                            i.getString("userProfilePhoto").toString(),
                            i.getString("uid").toString())
                        contactInfo.add(contact)
                        chatsAdapter = ChatsAdapter(context as Activity,contactInfo)
                        rvContact.adapter = chatsAdapter
                        rvContact.layoutManager = LinearLayoutManager(context)
                    }
                }
            }
        }

        return view
    }
}