package com.example.chat.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.chat.Channel
import com.example.chat.PagerAdapter
import com.example.chat.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ChannelsMainFragment : androidx.fragment.app.Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_channels_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager = view.findViewById<ViewPager2>(R.id.fragmentViewPager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)

        val channels = listOf(getMyChannels(), getAllChannels())
        val adapter = PagerAdapter(channels)
        viewPager.adapter = adapter

        val tabs: List<String> = listOf("Subscribed", "All streams")

        if (tabLayout != null && viewPager != null) {
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabs[position]
            }.attach()
        }
    }

    private fun getAllChannels(): List<Channel> {
        return listOf(
                Channel("General", listOf()),
                Channel("Development", listOf()),
                Channel("Coding", listOf()),
                Channel("Chess", listOf()),
                Channel("Android", listOf()),
                Channel("Design", listOf())
        )
    }

    private fun getMyChannels(): List<Channel> {
        return listOf(
                Channel("Coding", listOf()),
                Channel("Chess", listOf()),
                Channel("Android", listOf())
        )
    }
}
