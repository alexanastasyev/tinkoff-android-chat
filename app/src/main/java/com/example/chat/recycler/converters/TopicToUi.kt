package com.example.chat.recycler.converters

import com.example.chat.entities.Topic
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.uis.TopicUi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

fun convertTopicToUi(topics: List<Topic>) : List<ViewTyped> {
    val resultObservable = Observable.fromIterable(topics)
        .subscribeOn(Schedulers.newThread())
        .map {
            TopicUi(
                it.name
            )
        }
    return resultObservable.toList().blockingGet()
}