package com.example.chat.activities

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.chat.PagerAdapter
import com.example.chat.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ChannelsFragment : androidx.fragment.app.Fragment() {

    companion object {
        private var viewPager: ViewPager2? = null
        private var tabLayout: TabLayout? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inflater.inflate(R.layout.fragment_channels, container, true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val texts = listOf("My channels", "All channels")
        val adapter = PagerAdapter(texts)
        viewPager = activity?.findViewById(R.id.fragmentViewPager)
        viewPager?.adapter = adapter
        tabLayout = activity?.findViewById(R.id.tabLayout)

        val tabs: List<String> = listOf("Subscribed", "All streams")

        if (tabLayout != null && viewPager != null) {
            TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
                tab.text = tabs[position]
            }.attach()
        }
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        val texts = listOf("My channels", "All channels")
        val adapter = PagerAdapter(texts)
        viewPager?.adapter = adapter

        val tabs: List<String> = listOf("Subscribed", "All streams")
        if (tabLayout != null && viewPager != null) {
            TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
                tab.text = tabs[position]
            }.attach()
        }
    }
}
