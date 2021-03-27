package com.example.chat.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.chat.PagerAdapter
import com.example.chat.R
import com.example.chat.entities.Channel
import com.example.chat.entities.Contact
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ChannelsMainFragment : androidx.fragment.app.Fragment() {
    private val disposeBag = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        return inflater.inflate(R.layout.fragment_channels_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager = view.findViewById<ViewPager2>(R.id.fragmentViewPager)

        val myChannels = mutableListOf<Channel>()
        val allChannels = mutableListOf<Channel>()

        val channels = listOf(myChannels, allChannels)
        val adapter = PagerAdapter(channels)
        viewPager.adapter = adapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val tabs: List<String> = listOf("Subscribed", "All streams")

        if (tabLayout != null && viewPager != null) {
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabs[position]
            }.attach()
        }

        val myChannelsDisposable = getMyChannels()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ channel ->
                myChannels.add(channel)
                adapter.notifyDataSetChanged()
            },
            {
                Toast.makeText(this.context, getString(R.string.error_receive_channels), Toast.LENGTH_SHORT).show()
            })
        disposeBag.add(myChannelsDisposable)

        val allChannelsDisposable = getAllChannels()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ channel ->
                allChannels.add(channel)
                adapter.notifyDataSetChanged()
            },
            {
                Toast.makeText(this.context, getString(R.string.error_receive_channels), Toast.LENGTH_SHORT).show()
            })
        disposeBag.add(allChannelsDisposable)
    }

    private fun getAllChannels(): Observable<Channel> {
        return Observable.create{ subscriber ->
            for (i in 1..10) {
                val channel = Channel("Channel $i", i.toLong())
                subscriber.onNext(channel)
            }
            subscriber.onComplete()
        }
    }

    private fun getMyChannels(): Observable<Channel> {
        return Observable.create{ subscriber ->
            for (i in 1..10 step 3) {
                val channel = Channel("Channel $i", i.toLong())
                subscriber.onNext(channel)
            }
        }
    }

    override fun onDestroyView() {
        disposeBag.clear()
        super.onDestroyView()
    }
}
