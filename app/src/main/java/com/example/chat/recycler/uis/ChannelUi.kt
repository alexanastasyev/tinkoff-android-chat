package com.example.chat.recycler.uis

import com.example.chat.R
import com.example.chat.recycler.ViewTyped

class ChannelUi(
        var name: String,
        var isExpanded: Boolean = false,
        override val viewType: Int = R.layout.item_channel,
        override val uid: String = "channel$name"
) : ViewTyped