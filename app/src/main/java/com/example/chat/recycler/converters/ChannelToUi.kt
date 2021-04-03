package com.example.chat.recycler.converters

import com.example.chat.entities.Channel
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.uis.ChannelUi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

fun convertChannelToUi(channels: List<Channel>) : List<ViewTyped> {
    val resultObservable = Observable.fromIterable(channels)
        .subscribeOn(Schedulers.newThread())
        .map { ChannelUi(
            "#${it.name}"
        ) }
    return resultObservable.toList().blockingGet()
}