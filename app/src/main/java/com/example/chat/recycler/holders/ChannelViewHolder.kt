package com.example.chat.recycler.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.chat.R
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.uis.ChannelUi

class ChannelViewHolder (
    view: View,
    click: ((View) -> Unit)?,
) : BaseViewHolder<ChannelUi>(view) {

    private val channelViewHolder = view.findViewById<TextView>(R.id.channelName)
    private val imageArrow = view.findViewById<ImageView>(R.id.imageArrow)

    init {
        if (click != null) {
            imageArrow.setOnClickListener(click)
        }
    }

    override fun bind(item: ChannelUi) {
        channelViewHolder.text = item.name
        if (item.isExpanded) {
            imageArrow.setImageResource(R.drawable.arrow_up)
        } else {
            imageArrow.setImageResource((R.drawable.arrow_down))
        }
    }
}