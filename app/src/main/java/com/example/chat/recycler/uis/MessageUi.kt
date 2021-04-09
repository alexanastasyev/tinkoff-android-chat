package com.example.chat.recycler.uis

import com.example.chat.R
import com.example.chat.entities.Emoji
import com.example.chat.recycler.ViewTyped

class MessageUi(
        var messageId: Long,
        var text: String,
        var author: String,
        var authorId: Long,
        var avatarUrl: String?,
        var reactions: List<Pair<Emoji, Int>>,
        var isEmojiSelected: List<Boolean>,
        var date: String,
        override val viewType: Int = R.layout.item_message,
        override val uid: String = "message$messageId"
) : ViewTyped