package com.skappstech.chat.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skappstech.chat.MessageActivity
import com.skappstech.chat.R
import com.squareup.picasso.Picasso

class ChatsAdapter (val context: Context, private val contactList: ArrayList<User>)
    : RecyclerView.Adapter<ChatsAdapter.ContactsViewHolder>() {
    class ContactsViewHolder (view:View):RecyclerView.ViewHolder(view){
        val name : TextView = view.findViewById(R.id.nameContact)
        val email : TextView = view.findViewById(R.id.emailContact)
        val status : TextView = view.findViewById(R.id.statusContact)
        val image : ImageView = view.findViewById(R.id.profileContact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val contactView = LayoutInflater.from(parent.context).inflate(R.layout.rv_contacts,parent,false)
        return ContactsViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val list = contactList[position]
        holder.name.text = list.profileName
        holder.email.text = list.profileEmail
        holder.status.text = list.profileStatus
        Picasso.get().load(list.profilePicture).placeholder(R.drawable.user).into(holder.image)

        holder.itemView.setOnClickListener {
            val intent = Intent(context,MessageActivity::class.java)

            intent.putExtra("name",list.profileName)
            intent.putExtra("uid", list.uid)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }
}