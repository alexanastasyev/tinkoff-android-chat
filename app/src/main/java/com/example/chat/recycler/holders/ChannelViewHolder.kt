package com.example.chat.recycler.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.chat.R
import com.example.chat.recycler.uis.ChannelUi

class ChannelViewHolder (
    view: View,
    onClickListener: ((View) -> Unit)?,
) : BaseViewHolder<ChannelUi>(view) {

    private val textViewChannelName = view.findViewById<TextView>(R.id.channelName)
    private val imageArrow = view.findViewById<ImageView>(R.id.imageArrow)

    init {
        if (onClickListener != null) {
            view.setOnClickListener(onClickListener)
        }
    }

    override fun bind(item: ChannelUi) {
        textViewChannelName.text = item.name
        if (item.isExpanded) {
            imageArrow.setImageResource(R.drawable.arrow_up)
        } else {
            imageArrow.setImageResource((R.drawable.arrow_down))
        }
    }
}