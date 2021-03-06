package com.example.chat.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.viewpager2.widget.ViewPager2
import com.example.chat.Database
import com.example.chat.recycler.PagerAdapter
import com.example.chat.R
import com.example.chat.entities.Channel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

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

    companion object {
        private const val SEARCH_DELAY_MILLISECONDS = 500L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager = view.findViewById<ViewPager2>(R.id.fragmentViewPager)

        val myChannels = mutableListOf<Channel>()
        val allChannels = mutableListOf<Channel>()

        val channels = listOf(myChannels, allChannels)
        val adapter = PagerAdapter(channels)
        viewPager.adapter = adapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val tabs: List<String> = listOf(getString(R.string.subscribed), getString(R.string.all_channels))

        if (tabLayout != null && viewPager != null) {
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabs[position]
            }.attach()
        }

        val myChannelsDisposable = Database.getMyChannels()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ channel ->
                myChannels.add(channel)
                adapter.notifyItemChanged(0)
            },
            {
                Toast.makeText(this.context, getString(R.string.error_receive_channels), Toast.LENGTH_SHORT).show()
            })
        disposeBag.add(myChannelsDisposable)

        val allChannelsDisposable = Database.getAllChannels()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ channel ->
                allChannels.add(channel)
                adapter.notifyItemChanged(1)
            },
            {
                Toast.makeText(this.context, getString(R.string.error_receive_channels), Toast.LENGTH_SHORT).show()
            })
        disposeBag.add(allChannelsDisposable)

        val subject = PublishSubject.create<String>()

        val editTextSearchChannels = view.findViewById<EditText>(R.id.editTextSearchChannels)
        editTextSearchChannels.addTextChangedListener{str ->
            subject.onNext(str.toString().trim())
        }

        val disposable = subject
                .distinctUntilChanged()
                .debounce(SEARCH_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
                .subscribe{ str ->
                    adapter.filterChannels(str, tabLayout.selectedTabPosition)
                }
        disposeBag.add(disposable)

        val imageSearchChannels = view.findViewById<ImageView>(R.id.imageSearchChannels)
        imageSearchChannels.setOnClickListener {
            adapter.filterChannels(editTextSearchChannels.text.toString().trim(), tabLayout.selectedTabPosition)
        }
    }

    override fun onDestroyView() {
        disposeBag.clear()
        super.onDestroyView()
    }
}
