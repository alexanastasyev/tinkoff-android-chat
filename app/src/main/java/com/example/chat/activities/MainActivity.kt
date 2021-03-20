package com.example.chat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chat.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, ChannelsMainFragment(), "channels")
            .commit()

        findViewById<BottomNavigationView>(R.id.bottomNavigation).setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.channels -> {
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragmentContainer, ChannelsMainFragment(), "channels")
                        .commit()
                    true
                }
                R.id.people -> {
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragmentContainer, PeopleFragment(), "people")
                        .commit()
                    true
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragmentContainer, ProfileFragment(), "profile")
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}