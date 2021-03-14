package com.example.chat.recycler

import android.view.View
import com.example.chat.R
import com.example.chat.recycler.holders.BaseViewHolder
import com.example.chat.recycler.holders.MessageViewHolder

class ChatHolderFactory(
        private val click: ((View) -> Unit)? = null,
        private val action: ((View) -> Unit)? = null
    ) : HolderFactory() {

    override fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>? {
        return when (viewType) {
            R.layout.item_message -> MessageViewHolder(view, click, action)
            else -> null
        }
    }
}