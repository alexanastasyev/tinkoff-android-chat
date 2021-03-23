package com.example.chat.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.chat.PagerAdapter
import com.example.chat.R
import com.example.chat.entities.Channel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ChannelsMainFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title = "Search..."
        (activity as AppCompatActivity).supportActionBar?.show()
        return inflater.inflate(R.layout.fragment_channels_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager = view.findViewById<ViewPager2>(R.id.fragmentViewPager)
        val channels = listOf(getMyChannels(), getAllChannels())
        val adapter = PagerAdapter(channels)
        viewPager.adapter = adapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val tabs: List<String> = listOf("Subscribed", "All streams")

        if (tabLayout != null && viewPager != null) {
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabs[position]
            }.attach()
        }
    }

    private fun getAllChannels(): List<Channel> {
        return listOf(
                Channel("General", 1),
                Channel("Development", 2),
                Channel("Coding", 3),
                Channel("Chess", 4),
                Channel("Android", 5),
                Channel("Design", 6),
                Channel("Channel 4", 7),
                Channel("Channel 5", 8),
                Channel("Channel 6", 9),
                Channel("Channel 7", 10)
        )
    }

    private fun getMyChannels(): List<Channel> {
        return listOf(
                Channel("Coding", 3),
                Channel("Chess", 4),
                Channel("Android", 5),
                Channel("Channel 4", 7),
                Channel("Channel 5", 8),
                Channel("Channel 6", 9),
                Channel("Channel 7", 10)
        )
    }
}
