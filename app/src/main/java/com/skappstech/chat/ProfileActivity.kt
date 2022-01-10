package com.skappstech.chat

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.skappstech.chat.databinding.ActivityProfileBinding
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var fstore: FirebaseFirestore
    private lateinit var db : DocumentReference
    private lateinit var userid : String
    private lateinit var image: ByteArray
    private lateinit var storageReference: StorageReference
    private val register = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
        uploadImage(it)
    }

    private lateinit var toolbar1 : androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        toolbar1 = findViewById(R.id.toolbar2)
        toolbar1.title = "Profile"

        auth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()
        userid = auth.currentUser!!.uid
        storageReference = FirebaseStorage.getInstance().reference.child("$userid/profilePhoto")


         db = fstore.collection("users").document(userid)
         db.addSnapshotListener{value,error->
            if (error!=null){
                Log.d("Error","Unable to fetch data")
            }else{
                binding.tvNameProfile.text = value?.getString("userName")
                binding.tvEmailProfile.text = value?.getString("userEmail")
                binding.tvStatusProfile.text = value?.getString("userStatus")
                Picasso.get().load(value?.getString("userProfilePhoto")).placeholder(R.drawable.user).into(binding.profilePicture)
            }
        }

        binding.btnUpdateProfile.visibility = View.VISIBLE
        binding.btnUpdateProfile.setOnClickListener {
            binding.tvNameProfile.visibility = View.GONE
            binding.tvEmailProfile.visibility = View.GONE
            binding.tvStatusProfile.visibility = View.GONE
            binding.etNameProfile.visibility = View.VISIBLE
            binding.etStatusProfile.visibility = View.VISIBLE
            binding.btnSaveProfile.visibility = View.VISIBLE
            binding.btnUpdateProfile.visibility = View.GONE
            binding.etNameProfile.text = Editable.Factory.getInstance().newEditable(binding.tvNameProfile.text.toString())
            binding.etStatusProfile.text = Editable.Factory.getInstance().newEditable(binding.tvStatusProfile.text.toString())
        }

        binding.btnSaveProfile.setOnClickListener {
            binding.tvNameProfile.visibility = View.VISIBLE
            binding.tvEmailProfile.visibility = View.VISIBLE
            binding.tvStatusProfile.visibility = View.VISIBLE
            binding.etNameProfile.visibility = View.GONE
            binding.etStatusProfile.visibility = View.GONE
            binding.btnSaveProfile.visibility = View.GONE
            binding.btnUpdateProfile.visibility = View.VISIBLE
            val obj = mutableMapOf<String,String>()
            obj["userName"] = binding.etNameProfile.text.toString()
            obj["userStatus"] = binding.etStatusProfile.text.toString()
            db.update(obj as Map<String,String>).addOnSuccessListener{
                Log.d("Success","Data Successfully Updated")
            }
        }

        binding.profileAdd.setOnClickListener {
            capturePhoto()
        }
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
                obj["userProfilePhoto"] = it.toString()
                db.update(obj as Map<String, Any>).addOnSuccessListener {
                    Log.d("onSuccess","ProfilePictureUploaded")
                }
            }
        }
    }
}