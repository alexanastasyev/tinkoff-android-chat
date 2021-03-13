package com.example.chat

import android.service.voice.AlwaysOnHotwordDetector
import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class BaseViewHolder<T: ViewTyped>(
        val containerView: View
        ) : RecyclerView.ViewHolder(containerView) {
    open fun bind(item: T) = Unit
    open fun bind(item: T, payload: List<Any>) = Unit
}