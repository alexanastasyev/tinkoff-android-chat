package com.example.chat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.chat.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        actionBar?.hide()

        val toolbar = findViewById<Toolbar>(R.id.toolbarMain)
        setSupportActionBar(toolbar)

        showChannelsFragment()

        findViewById<BottomNavigationView>(R.id.bottomNavigation).setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.channels -> {
                    showChannelsFragment()
                    true
                }
                R.id.people -> {
                    showPeopleFragment()
                    true
                }
                R.id.profile -> {
                    showProfileFragment()
                    true
                }
                else -> false
            }
        }
    }

    private fun showChannelsFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, ChannelsMainFragment())
            .commit()
    }

    private fun showPeopleFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, PeopleFragment())
            .commit()
    }

    private fun showProfileFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, ProfileFragment())
            .commit()
    }
}