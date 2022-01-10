package com.skappstech.chat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skappstech.chat.R
import com.squareup.picasso.Picasso

class StatusAdapter (val context: Context,private val statusList:ArrayList<UserStatus>)
    : RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {
    class StatusViewHolder (view:View):RecyclerView.ViewHolder(view){
        val name:TextView = view.findViewById(R.id.nameStatus)
        val email:TextView = view.findViewById(R.id.emailStatus)
        val image:ImageView = view.findViewById(R.id.profileStatus)
        val imgStatus:ImageView = view.findViewById(R.id.imgStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val statusView = LayoutInflater.from(parent.context).inflate(R.layout.rv_status,parent,false)
        return StatusViewHolder(statusView)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val list = statusList[position]
        holder.name.text = list.profileName
        holder.email.text = list.profileEmail
        Picasso.get().load(list.profilePicture).placeholder(R.drawable.user).into(holder.image)
        Picasso.get().load(list.statusPicture).placeholder(R.drawable.user).into(holder.imgStatus)
    }

    override fun getItemCount(): Int {
        return statusList.size
    }


}