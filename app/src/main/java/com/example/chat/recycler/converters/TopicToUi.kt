package com.example.chat.recycler.converters

import com.example.chat.entities.Topic
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.holders.TopicUi

fun topicToUi(topics: List<Topic>) : List<ViewTyped> {

    val topicUis: ArrayList<TopicUi> = ArrayList()
    for (topic in topics) {
        topicUis.add(TopicUi(
                topic.name
        ))
    }
    return topicUis
}