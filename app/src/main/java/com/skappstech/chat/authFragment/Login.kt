package com.skappstech.chat.authFragment

import android.app.Activity
import android.content.Intent
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.skappstech.chat.R

class Login : Fragment() {
    private lateinit var emailLogin: EditText
    private lateinit var passLogin: EditText
    private lateinit var loginButton: Button
//    private lateinit var googleButton: Button
    private lateinit var progressbar2: ProgressBar
//    private lateinit var googleSignInOptions: GoogleSignInOptions
//    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var resultLaunch : ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        emailLogin = view.findViewById(R.id.et_email_login)
        passLogin = view.findViewById(R.id.et_pass_login)
        loginButton = view.findViewById(R.id.btn_login)
//        googleButton = view.findViewById(R.id.btn_google)
        progressbar2 = view.findViewById(R.id.progressBar2)

        loginButton.setOnClickListener {
            val email = emailLogin.text.toString()
            val password = passLogin.text.toString()
            if (TextUtils.isEmpty(email)){
                emailLogin.error = "Email Required!"
            }else if (TextUtils.isEmpty(password)){
                passLogin.error = "Password Required!"
            }else{
                progressbar2.visibility = View.VISIBLE
                signIn(email,password)
            }
        }

//        googleButton.setOnClickListener {
//            createRequest()
//        }

        resultLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if (result.resultCode==Activity.RESULT_OK){
                val launchData = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(launchData)
                try {
                    val account = task.getResult(ApiException::class.java)
                    Log.d("Gmail ID","firebaseAuthWith Google : $account")
                    firebaseAuthWithGoogle(account?.idToken)
                }
                catch (e:ApiException)
                {
                    Log.w("Error","Google Sign IN Failed",e)
                }
            }
        }
        return view
    }

//    private fun createRequest() {
//        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("448649239556-lr37vj137o8222r0a6nag481qn4s9ndj.apps.googleusercontent.com")
//            .requestEmail()
//            .build()
//        mGoogleSignInClient = context.let { GoogleSignIn.getClient(it!!, googleSignInOptions) }
//        resultLaunch.launch(Intent(mGoogleSignInClient.signInIntent))
//    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        FirebaseAuth.getInstance().signInWithCredential(credential)
//            .addOnCompleteListener {
//                val acct = GoogleSignIn.getLastSignedInAccount(requireContext())
//                if (acct != null) {
//                    val personName = acct.displayName!!
//                    val personEmail = acct.email!!
//                    val personPhoto = acct.photoUrl!!
//                    val objLogin = mutableMapOf<String, String>()
//                    objLogin["userEmail"] = personEmail
//                    objLogin["userProfilePhoto"] = personPhoto.toString()
//                    objLogin["userName"] = personName
//                    val userId = FirebaseAuth.getInstance().currentUser?.uid
//                    FirebaseFirestore.getInstance().collection("users").document(userId.toString())
//                        .set(objLogin).addOnSuccessListener {
//                            Log.d("onSuccess", "Successfully Google Login")
//                        }
//                }
//            }
    }

    private fun signIn(email:String,pass:String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass).addOnCompleteListener {task->
            if (task.isSuccessful){
                Toast.makeText(context,"Login Successfully",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"Login failed(Incorrect email/password)", Toast.LENGTH_LONG).show()
                progressbar2.visibility = View.GONE
            }
        }
    }

}