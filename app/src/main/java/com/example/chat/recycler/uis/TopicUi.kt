package com.example.chat.recycler.uis

import com.example.chat.R
import com.example.chat.recycler.ViewTyped

class TopicUi(
        var name: String,
        override val viewType: Int = R.layout.item_topic,
        override val uid: String = "topic$name"
) : ViewTyped