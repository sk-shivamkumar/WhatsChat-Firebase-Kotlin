package com.skappstech.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.skappstech.chat.databinding.ActivityMainBinding
import com.skappstech.chat.mainFragment.Chats
import com.skappstech.chat.mainFragment.Status

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager2 : ViewPager2
    private lateinit var tabLayout : TabLayout
    private lateinit var toolbar : androidx.appcompat.widget.Toolbar
    private lateinit var appPagerAdapter : AppPagerAdapter
    private lateinit var auth : FirebaseAuth
    private val titles = arrayListOf("Chats","Status")
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        toolbar = findViewById(R.id.toolbar)
        tabLayout = findViewById(R.id.tablayout)
        viewPager2 = findViewById(R.id.viewPager2)
        auth = FirebaseAuth.getInstance()
        toolbar.title = "WhatsChat"

        setSupportActionBar(toolbar)

        appPagerAdapter = AppPagerAdapter(this)
        viewPager2.adapter = appPagerAdapter
        TabLayoutMediator(tabLayout,viewPager2){
            tab,position->
            tab.text = titles[position]
        }.attach()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.profile -> {
                startActivity(Intent(this,ProfileActivity::class.java).putExtra("OptionName","profile"))
            }
            R.id.logout -> {
                auth.signOut()
                startActivity(Intent(this,AuthenticationActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

class AppPagerAdapter (fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position)
        {
            0-> Chats()
            1-> Status()
            else -> Chats()
        }
    }


}
