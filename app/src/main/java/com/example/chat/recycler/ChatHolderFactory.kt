package com.example.chat.recycler

import android.view.View
import com.example.chat.R
import com.example.chat.recycler.holders.*

class ChatHolderFactory(
        private val click: ((View) -> Unit)? = null,
        private val action: ((View) -> Unit)? = null,
        private val shouldShowDate: ((View) -> Boolean)? = null
    ) : HolderFactory() {

    override fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>? {
        return when (viewType) {
            R.layout.item_message -> shouldShowDate?.let { it -> MessageViewHolder(view, click, action, it) }
            R.layout.item_contact -> ContactViewHolder(view, click)
            R.layout.item_channel -> ChannelViewHolder(view, click)
            R.layout.item_topic -> TopicViewHolder(view, click)
            else -> null
        }
    }
}