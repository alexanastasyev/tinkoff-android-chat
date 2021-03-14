package com.example.chat

import android.view.View
import android.widget.TextView

class TextUi(
        val text: String,
        override val viewType: Int = R.layout.item_text
) : ViewTyped

class TextViewHolder(view: View, click: ((View) -> Unit)?) : BaseViewHolder<TextUi>(view) {
    val textHolder = view.findViewById<TextView>(R.id.textHolder)

    init {
        if (click != null) {
            textHolder.setOnClickListener(click)
        }
    }

    override fun bind(item: TextUi) {
        textHolder.text = item.text
    }
}