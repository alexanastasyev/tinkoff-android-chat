package com.example.chat.recycler.holders

import android.view.View
import android.widget.TextView
import com.example.chat.R
import com.example.chat.recycler.ViewTyped

class TopicUi (
        var name: String,
        override val viewType: Int = R.layout.item_topic
    ) : ViewTyped

class TopicViewHolder (
        view: View,
        click: ((View) -> Unit)?,
) : BaseViewHolder<TopicUi>(view) {

    private val topicViewHolder = view.findViewById<TextView>(R.id.topicName)

    init {
        if (click != null) {
            topicViewHolder.setOnClickListener(click)
        }
    }

    override fun bind(item: TopicUi) {
        topicViewHolder.text = item.name
    }

}