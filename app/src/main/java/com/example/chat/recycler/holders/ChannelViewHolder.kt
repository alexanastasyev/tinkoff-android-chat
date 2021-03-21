package com.example.chat.recycler.holders

import android.view.View
import android.widget.TextView
import com.example.chat.R
import com.example.chat.recycler.ViewTyped

class ChannelUi (
        var name: String,
        var topics: List<String>,
        override val viewType: Int = R.layout.item_channel
        ) : ViewTyped

class ChannelViewHolder (
        view: View,
        click: ((View) -> Unit)?,
) : BaseViewHolder<ChannelUi>(view) {

    private val channelViewHolder = view.findViewById<TextView>(R.id.channelName)

    init {
        if (click != null) {
            channelViewHolder.setOnClickListener(click)
        }
    }

    override fun bind(item: ChannelUi) {
        channelViewHolder.text = item.name
    }

}