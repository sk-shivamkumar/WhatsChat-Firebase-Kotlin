package com.skappstech.chat.authFragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.skappstech.chat.R
class SignUp : Fragment() {
    private lateinit var emailSignup : EditText
    private lateinit var nameSignup :EditText
    private lateinit var passSignup : EditText
    private lateinit var signUpButton : Button
    private lateinit var progressbar : ProgressBar
    private lateinit var fauth : FirebaseAuth
    private lateinit var fstore : FirebaseFirestore
    private lateinit var db : DocumentReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        emailSignup = view.findViewById(R.id.et_email_signup)
        nameSignup = view.findViewById(R.id.et_name_signup)
        passSignup = view.findViewById(R.id.et_pass_signup)
        signUpButton = view.findViewById(R.id.btn_signup)
        progressbar = view.findViewById(R.id.progressBar)
        fauth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()

        signUpButton.setOnClickListener {

            val name = nameSignup.text.toString()
            val email = emailSignup.text.toString()
            val password = passSignup.text.toString()
            if (TextUtils.isEmpty(name)){
                nameSignup.error = "Name Required!"
            }else if (TextUtils.isEmpty(email)){
                emailSignup.error = "Email Required!"
            }else if (TextUtils.isEmpty(password)){
                passSignup.error = "Password Required!"
            }else if (password.length<6){
                passSignup.error = "Password must be greater than 6 characters"
            }else{
                progressbar.visibility = View.VISIBLE
                createAccount(name,email,password)
            }
        }
        return view
    }

    private fun createAccount(name : String, email : String, pass : String) {

        fauth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener { task->
            if (task.isSuccessful){
                val  userinfo = fauth.currentUser?.uid
                db = fstore.collection("users").document(userinfo.toString())
                val obj = mutableMapOf<String,String>()
                obj["userEmail"] = email
                obj["userPassword"] = pass
                obj["userName"]= name
                obj["uid"]= fauth.currentUser!!.uid
                db.set(obj).addOnSuccessListener {
                    Log.d("onSucess","User Created Successfully")
                    progressbar.visibility = View.GONE
                }
                Toast.makeText(context,"Account Created Successfully", Toast.LENGTH_SHORT).show()
            }
        }

    }
}