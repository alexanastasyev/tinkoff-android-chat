package com.example.chat.screens.channels

import android.content.Context
import androidx.room.Room
import com.example.chat.database.AppDatabase
import com.example.chat.entities.Channel
import com.example.chat.internet.ZulipService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ChannelsFragmentPresenter(private val channelsView: ChannelsView, context: Context) {
    private val compositeDisposable = CompositeDisposable()

    private val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "database"
    ).fallbackToDestructiveMigration().build()

    fun loadData() {
        loadMyChannelsFromDataBase()
        loadMyChannelsFromServer()
        loadAllChannelsFromDatabase()
        loadAllChannelsFromServer()
    }

    private fun loadAllChannelsFromServer() {
        val disposable = Single.fromCallable { ZulipService.getAllChannels() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ channels ->
                if (channels != null) {
                    channelsView.updateAllChannels(channels)
                    replaceChannelsInDatabase(channels)
                } else {
                    channelsView.showErrorOfLoading()
                }
            }, {
                channelsView.showErrorOfLoading()
            }
        )
        compositeDisposable.add(disposable)
    }

    private fun loadAllChannelsFromDatabase() {
        val disposable = Single.fromCallable { db.channelDao().getAll() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ channels ->
               channelsView.updateAllChannels(channels)
            }, {
                channelsView.showErrorOfLoading()
            })
        compositeDisposable.add(disposable)
    }

    private fun loadMyChannelsFromServer() {
        val disposable = Single.fromCallable { ZulipService.getMyChannels() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ channels ->
                if (channels != null) {
                    channelsView.updateMyChannels(channels)
                    replaceChannelsInDatabase(channels)
                } else {
                    channelsView.showErrorOfLoading()
                }
            }, {
                channelsView.showErrorOfLoading()
            })
        compositeDisposable.add(disposable)
    }

    private fun loadMyChannelsFromDataBase() {
        val disposable = Single.fromCallable { db.channelDao().getSubscribed() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ channels ->
                channelsView.updateMyChannels(channels)
            }, {
                channelsView.showErrorOfLoading()
            })
        compositeDisposable.add(disposable)
    }

    private fun addChannelsToDatabase(channels: List<Channel>) {
        val disposable = Single.just(Unit)
            .subscribeOn(Schedulers.io())
            .subscribe({
                for (channel in channels) {
                    if (!db.channelDao().contains(channel.id)) {
                        db.channelDao().insert(channel)
                    }
                }
            }, {
                channelsView.showErrorOfSaving()
            })
        compositeDisposable.add(disposable)
    }

    private fun replaceChannelsInDatabase(channels: List<Channel>) {
        val disposable = Single.just(Unit)
            .subscribeOn(Schedulers.io())
            .subscribe({
                db.channelDao().deleteAll()
                addChannelsToDatabase(channels)
            }, {
                channelsView.showErrorOfSaving()
            })
        compositeDisposable.add(disposable)
    }

    fun disposeDisposable() {
        compositeDisposable.dispose()
    }
}