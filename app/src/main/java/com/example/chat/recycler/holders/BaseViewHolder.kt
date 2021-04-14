package com.example.chat.recycler.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.recycler.ViewTyped

open class BaseViewHolder<T : ViewTyped>(
    containerView: View
) : RecyclerView.ViewHolder(containerView) {
    open fun bind(item: T) = Unit
    open fun bind(item: T, payload: List<Any>) = Unit
}