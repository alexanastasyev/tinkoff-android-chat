package com.example.chat

import android.view.View

class TfsHolderFactory(private val click: (View) -> Unit) : HolderFactory() {

    override fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>? {
        return when (viewType) {
            R.layout.item_text -> TextViewHolder(view, click)
            R.layout.item_message -> MessageViewHolder(view)
            else -> null
        }
    }
}