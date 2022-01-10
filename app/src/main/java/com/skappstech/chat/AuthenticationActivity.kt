package com.skappstech.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.skappstech.chat.authFragment.Login
import com.skappstech.chat.authFragment.SignUp

class AuthenticationActivity : AppCompatActivity(),FirebaseAuth.AuthStateListener {
    private lateinit var viewPager : ViewPager2
    private lateinit var tabLayout : TabLayout
    private lateinit var viewPagerAdapter : AuthenticationPagerAdapter
    private val titles = arrayListOf("Login","SignUp")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        viewPager = findViewById(R.id.viewpagerauth)
        tabLayout = findViewById(R.id.tablayoutauth)
        viewPagerAdapter = AuthenticationPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout,viewPager){
            tab,position->
            tab.text = titles[position]
        }.attach()
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        if (p0.currentUser!=null){
            startMainActivity()
        }
    }
    override fun onStart(){
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(this)
        if (FirebaseAuth.getInstance().currentUser!=null){
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()

    }
    override fun onStop(){
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(this)

    }
}

class AuthenticationPagerAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> Login()
            1-> SignUp()
            else -> Login() }
    }

}
