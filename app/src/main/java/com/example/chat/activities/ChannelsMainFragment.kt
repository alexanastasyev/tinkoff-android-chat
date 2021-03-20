package com.example.chat.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
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
        inflater.inflate(R.layout.fragment_channels_main, container, true)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.fragmentViewPager)
        val tabLayout = activity?.findViewById<TabLayout>(R.id.tabLayout)

        val texts = listOf("My channels", "All channels")
        val adapter = PagerAdapter(texts)
        viewPager?.adapter = adapter

        val tabs: List<String> = listOf("Subscribed", "All streams")

        if (tabLayout != null && viewPager != null) {
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabs[position]
            }.attach()
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
