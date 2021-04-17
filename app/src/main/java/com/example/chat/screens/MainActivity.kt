package com.example.chat.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.chat.R
import com.example.chat.screens.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    companion object {
        private const val CHANNELS_FRAGMENT_TAG = "channels"
        private const val PEOPLE_FRAGMENT_TAG = "people"
        private const val PROFILE_FRAGMENT_TAG = "profile"
    }

    private val channelsFragment = ChannelsMainFragment()
    private val peopleFragment = PeopleFragment()
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        attachFragment(channelsFragment, CHANNELS_FRAGMENT_TAG)
    }

    private fun showPeopleFragment() {
        attachFragment(peopleFragment, PEOPLE_FRAGMENT_TAG)
    }

    private fun showProfileFragment() {
        attachFragment(profileFragment, PROFILE_FRAGMENT_TAG)
    }

    private fun attachFragment(fragment: Fragment, tag: String) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            fragmentTransaction.add(R.id.fragmentContainer, fragment, tag)
            fragmentTransaction.addToBackStack(tag)
        } else {
            val fragmentToShow = supportFragmentManager.findFragmentByTag(tag)
            if (fragmentToShow != null) {
                fragmentTransaction.show(fragmentToShow)
            }
        }
        supportFragmentManager.fragments.forEach {
            if (it != fragment && it.isAdded) {
                fragmentTransaction.hide(it)
            }
        }
        fragmentTransaction.commit()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        onCreate(savedInstanceState)
    }
}