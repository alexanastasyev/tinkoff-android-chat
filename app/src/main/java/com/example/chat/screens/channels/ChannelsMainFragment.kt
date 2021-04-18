package com.example.chat.screens.channels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.viewpager2.widget.ViewPager2
import com.example.chat.R
import com.example.chat.entities.Channel
import com.example.chat.recycler.PagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class ChannelsMainFragment : androidx.fragment.app.Fragment(), ChannelsView {
    private val compositeDisposable = CompositeDisposable()

    private val myChannels = mutableListOf<Channel>()
    private val allChannels = mutableListOf<Channel>()

    private val channels = listOf(myChannels, allChannels)
    private val adapter = PagerAdapter(channels)

    private lateinit var tabLayout: TabLayout

    private lateinit var presenter: ChannelsFragmentPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_channels_main, container, false)
    }

    companion object {
        private const val SEARCH_DELAY_MILLISECONDS = 500L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.hide()
        presenter = ChannelsFragmentPresenter(this, this.requireContext())
        val viewPager = view.findViewById<ViewPager2>(R.id.fragmentViewPager)
        viewPager.adapter = adapter
        tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val tabs: List<String> = listOf(getString(R.string.subscribed), getString(R.string.all_channels))
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()

        configureSearch(view)

        presenter.loadData()
    }

    private fun configureSearch(view: View) {
        val subject = PublishSubject.create<String>()

        val editTextSearchChannels = view.findViewById<EditText>(R.id.editTextSearchChannels)
        editTextSearchChannels.addTextChangedListener { str ->
            subject.onNext(str.toString().trim())
        }

        val disposable = subject
            .distinctUntilChanged()
            .debounce(SEARCH_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
            .subscribe { str ->
                adapter.filterChannels(str, tabLayout.selectedTabPosition)
            }
        compositeDisposable.add(disposable)

        val imageSearchChannels = view.findViewById<ImageView>(R.id.imageSearchChannels)
        imageSearchChannels.setOnClickListener {
            adapter.filterChannels(
                editTextSearchChannels.text.toString().trim(),
                tabLayout.selectedTabPosition
            )
        }
    }

    override fun onDestroyView() {
        compositeDisposable.clear()
        presenter.disposeDisposable()
        super.onDestroyView()
    }

    override fun updateMyChannels(channels: List<Channel>) {
        if (myChannelsHasChanged(channels)) {
            myChannels.clear()
            myChannels.addAll(channels)
            adapter.notifyDataSetChanged()
        }
        view?.findViewById<ProgressBar>(R.id.progressBarChannels)?.visibility = View.GONE
        view?.findViewById<ConstraintLayout>(R.id.channelsContentLayout)?.visibility = View.VISIBLE
    }

    private fun myChannelsHasChanged(newChannels: List<Channel>): Boolean {
        return !(myChannels.containsAll(newChannels) && newChannels.containsAll(myChannels))
    }

    override fun updateAllChannels(channels: List<Channel>) {
        if (allChannelsHasChanged(channels)) {
            allChannels.clear()
            allChannels.addAll(channels)
            adapter.notifyDataSetChanged()
        }
        view?.findViewById<ProgressBar>(R.id.progressBarChannels)?.visibility = View.GONE
        view?.findViewById<ConstraintLayout>(R.id.channelsContentLayout)?.visibility = View.VISIBLE
    }

    private fun allChannelsHasChanged(newChannels: List<Channel>): Boolean {
        return !(allChannels.containsAll(newChannels) && newChannels.containsAll(allChannels))
    }

    override fun showErrorOfLoading() {
        Toast.makeText(this.context, getString(R.string.cant_load_channels), Toast.LENGTH_SHORT).show()
    }

    override fun showErrorOfSaving() {
        Toast.makeText(this.context, getString(R.string.cant_save_channels), Toast.LENGTH_SHORT).show()
    }
}
