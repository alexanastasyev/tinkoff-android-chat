package com.example.chat.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.example.chat.recycler.holders.BaseViewHolder

abstract class HolderFactory : (ViewGroup, Int) -> BaseViewHolder<ViewTyped> {
    abstract fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>?

    final override fun invoke(viewGroup: ViewGroup, viewType: Int): BaseViewHolder<ViewTyped> {
        val view: View = viewGroup.inflate(viewType)
        return when (viewType) {
            // Here can be different simple view types (progress bar, etc)
            else -> checkNotNull(createViewHolder(view, viewType)) {
                "unknown viewType=" + viewGroup.resources.getResourceName(viewType)
            }
        } as BaseViewHolder<ViewTyped>
    }
}

fun <T: View> View.inflate (
    @LayoutRes
    layout: Int,
    root: ViewGroup? = this as? ViewGroup,
    attachToRoot: Boolean = false
): T {
    return LayoutInflater.from(context).inflate(layout, root, attachToRoot) as T
}