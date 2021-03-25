package com.example.chat.recycler.converters

import com.example.chat.entities.Topic
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.uis.TopicUi

fun topicToUi(topics: List<Topic>) : List<ViewTyped> {
    return topics.map { TopicUi(
        it.name
    ) }
}