package com.example.chat.recycler

import androidx.recyclerview.widget.DiffUtil

class DiffCallback: DiffUtil.ItemCallback<ViewTyped>() {
    override fun areItemsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
        return true
    }

    override fun getChangePayload(oldItem: ViewTyped, newItem: ViewTyped): Any? {
        return super.getChangePayload(oldItem, newItem)
    }
}