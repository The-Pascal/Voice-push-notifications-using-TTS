package com.example.bajajnotifications


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.bajajnotifications.databinding.ActivityMainBinding
import com.example.bajajnotifications.allNotifications.AllNotificationsFragment
import com.example.bajajnotifications.history.HistoryFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        val homeFragment : Fragment = AllNotificationsFragment()
        val historyFragment : Fragment = HistoryFragment()

        setCurrentFragment(homeFragment)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.home -> {
                    setCurrentFragment(homeFragment)
                    true
                }
                R.id.history -> {
                    setCurrentFragment(historyFragment)
                    true
                }
                else -> false
            }
        }

    }

    private fun setCurrentFragment(fragment: Fragment) =

    supportFragmentManager.beginTransaction().apply{
        replace(R.id.flFragment,fragment)
        commit()
    }



}