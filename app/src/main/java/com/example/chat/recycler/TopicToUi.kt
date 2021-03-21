package com.example.chat.recycler

import com.example.chat.Topic
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