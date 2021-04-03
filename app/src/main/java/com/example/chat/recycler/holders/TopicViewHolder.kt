package com.example.chat.recycler.holders

import android.view.View
import android.widget.TextView
import com.example.chat.R
import com.example.chat.recycler.uis.TopicUi

class TopicViewHolder (
    view: View,
    onClickListener: ((View) -> Unit)?,
) : BaseViewHolder<TopicUi>(view) {

    private val topicView = view.findViewById<TextView>(R.id.topicName)

    init {
        if (onClickListener != null) {
            topicView.setOnClickListener(onClickListener)
        }
    }

    override fun bind(item: TopicUi) {
        topicView.text = item.name
    }
}