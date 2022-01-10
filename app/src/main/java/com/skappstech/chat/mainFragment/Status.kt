package com.skappstech.chat.mainFragment

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.skappstech.chat.R
import com.skappstech.chat.adapters.StatusAdapter
import com.skappstech.chat.adapters.UserStatus
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class Status : Fragment() {
    private lateinit var statusAdapter: StatusAdapter
    private val statusInfo = arrayListOf<UserStatus>()
    private lateinit var fstore : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private lateinit var rvStatus : RecyclerView
    private lateinit var uploadStatus : ImageView
    private lateinit var addStatus : ImageView
    private lateinit var db : DocumentReference
    private lateinit var storageReference: StorageReference
    private lateinit var image: ByteArray
    private lateinit var userid : String
    private lateinit var deleteStatus : Button
    private val register = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
        uploadImage(it)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_status, container, false)
        rvStatus = view.findViewById(R.id.rvStatus)

        uploadStatus = view.findViewById(R.id.uploadStatus)
        addStatus = view.findViewById(R.id.statusAdd)
        deleteStatus = view.findViewById(R.id.deleteStatus)

        fstore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        userid = auth.currentUser!!.uid
        storageReference = FirebaseStorage.getInstance().reference.child("$userid/statusPhoto")

        db = fstore.collection("users").document(userid)
        db.addSnapshotListener{value, error->
            if (error!=null){
                Log.d("Error","Unable to fetch data")
            }else{
                Picasso.get().load(value?.getString("userStatusPhoto")).placeholder(R.drawable.user).into(uploadStatus)
            }
        }

        fstore.collection("users").get().addOnSuccessListener {
            if (!it.isEmpty){
                val listStatus = it.documents
                for(i in listStatus){
                    if (i.id== auth.currentUser?.uid){
                        Log.d("onFound","This is User Account")
                    } else{
                        if (i.getString("userStatusPhoto")!=null) {
                            val status = UserStatus(
                                i.getString("userName").toString(),
                                i.getString("userEmail").toString(),
                                i.getString("userProfilePhoto").toString(),
                                i.getString("userStatusPhoto").toString()
                            )
                            statusInfo.add(0, status)
                            statusAdapter = StatusAdapter(context as Activity, statusInfo)
                            rvStatus.adapter = statusAdapter
                            rvStatus.layoutManager = LinearLayoutManager(context)
                        }
                    }
                }
            }
        }

        deleteStatus.setOnClickListener {
            val  userinfo = FirebaseAuth.getInstance().currentUser?.uid
            val docRef = FirebaseFirestore.getInstance().collection("users").document(userinfo.toString())

            val updates = hashMapOf<String, Any>(
                "userStatusPhoto" to FieldValue.delete()
            )

            docRef.update(updates).addOnCompleteListener { }
        }

        addStatus.setOnClickListener {
            capturePhoto()
        }

        return view
    }

    private fun capturePhoto() {
        register.launch(null)
    }

    private fun uploadImage(it: Bitmap?) {
        val baos = ByteArrayOutputStream()
        it?.compress(Bitmap.CompressFormat.JPEG,100,baos)
        image = baos.toByteArray()
        storageReference.putBytes(image).addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener {
                val obj = mutableMapOf<String,String>()
                obj["userStatusPhoto"] = it.toString()
                db.update(obj as Map<String, Any>).addOnSuccessListener {
                    Log.d("onSuccess","StatusPictureUploaded")
                }
            }
        }
    }
}