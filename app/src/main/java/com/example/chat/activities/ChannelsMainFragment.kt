package com.example.chat.activities

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
import androidx.room.Room
import androidx.viewpager2.widget.ViewPager2
import com.example.chat.R
import com.example.chat.database.AppDatabase
import com.example.chat.entities.Channel
import com.example.chat.internet.ZulipService
import com.example.chat.recycler.PagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.Observable
import io.reactivex.Single
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
        return inflater.inflate(R.layout.fragment_channels_main, container, false)
    }

    companion object {
        private const val SEARCH_DELAY_MILLISECONDS = 500L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.hide()

        val viewPager = view.findViewById<ViewPager2>(R.id.fragmentViewPager)

        val myChannels = mutableListOf<Channel>()
        val allChannels = mutableListOf<Channel>()

        val channels = listOf(myChannels, allChannels)
        val adapter = PagerAdapter(channels)
        viewPager.adapter = adapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val tabs: List<String> =
            listOf(getString(R.string.subscribed), getString(R.string.all_channels))

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()

        val myChannelsDisposable = Single.fromCallable { ZulipService.getMyChannels() }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ channels ->
                if (channels != null) {

                    view.findViewById<ProgressBar>(R.id.progressBarChannels).visibility = View.GONE
                    view.findViewById<ConstraintLayout>(R.id.channelsContentLayout).visibility =
                        View.VISIBLE

                    myChannels.clear()
                    myChannels.addAll(0, channels)
                    adapter.notifyDataSetChanged()

                    val disposable = Observable.fromArray(channels)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            addChannelsToDatabase(channels)
                        }, {})
                    disposeBag.add(disposable)
                }
            },
                {
                    Toast.makeText(
                        this.context,
                        getString(R.string.error_receive_channels),
                        Toast.LENGTH_SHORT
                    ).show()
                })
        disposeBag.add(myChannelsDisposable)

        val allChannelsDisposable = Single.fromCallable { ZulipService.getAllChannels() }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ channels ->
                if (channels != null) {
                    allChannels.clear()
                    allChannels.addAll(0, channels)
                    adapter.notifyItemChanged(1)

                    val disposable = Observable.fromArray(channels)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            replaceChannelsInDatabase(channels)
                        }, {})
                    disposeBag.add(disposable)
                }
            },
                {
                    Toast.makeText(
                        this.context,
                        getString(R.string.error_receive_channels),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        disposeBag.add(allChannelsDisposable)

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
        disposeBag.add(disposable)

        val imageSearchChannels = view.findViewById<ImageView>(R.id.imageSearchChannels)
        imageSearchChannels.setOnClickListener {
            adapter.filterChannels(
                editTextSearchChannels.text.toString().trim(),
                tabLayout.selectedTabPosition
            )
        }

        val disposableGetSubscribedFromDb =
            Single.fromCallable { getSubscribedChannelsFromDatabase() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ channelsFromDatabase ->
                    if (channelsFromDatabase.isNotEmpty()) {
                        view.findViewById<ProgressBar>(R.id.progressBarChannels).visibility =
                            View.GONE
                        view.findViewById<ConstraintLayout>(R.id.channelsContentLayout).visibility =
                            View.VISIBLE

                        myChannels.addAll(0, channelsFromDatabase)
                        adapter.notifyDataSetChanged()
                    }
                }, {})
        disposeBag.add(disposableGetSubscribedFromDb)

        val disposableGetAllFromDb = Single.fromCallable { getAllChannelsFromDatabase() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ channelsFromDatabase ->
                if (channelsFromDatabase.isNotEmpty()) {

                    allChannels.addAll(0, channelsFromDatabase)
                    adapter.notifyDataSetChanged()
                }
            }, {})
        disposeBag.add(disposableGetAllFromDb)
    }

    private fun addChannelsToDatabase(channels: List<Channel>) {
        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "database"
        ).fallbackToDestructiveMigration().build()
        for (channel in channels) {
            db.channelDao().insert(channel)
        }
    }

    private fun replaceChannelsInDatabase(channels: List<Channel>) {
        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "database"
        ).fallbackToDestructiveMigration().build()
        db.channelDao().deleteAll()
        for (channel in channels) {
            db.channelDao().insert(channel)
        }
    }

    private fun getSubscribedChannelsFromDatabase(): List<Channel> {
        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "database"
        ).fallbackToDestructiveMigration().build()
        return db.channelDao().getSubscribed()
    }

    private fun getAllChannelsFromDatabase(): List<Channel> {
        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "database"
        ).fallbackToDestructiveMigration().build()
        return db.channelDao().getAll()
    }

    override fun onDestroyView() {
        disposeBag.clear()
        super.onDestroyView()
    }
}
