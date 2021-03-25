package com.example.chat.recycler.uis

import com.example.chat.R
import com.example.chat.entities.Reaction
import com.example.chat.recycler.ViewTyped
import java.util.*

class MessageUi(
        var messageId: Long,
        var text: String,
        var author: String,
        var authorId: Long,
        var avatarUrl: String?,
        var reactions: ArrayList<Reaction>,
        var date: Date,
        override val viewType: Int = R.layout.item_message
) : ViewTyped