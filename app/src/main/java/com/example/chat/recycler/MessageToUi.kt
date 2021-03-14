package com.example.chat.recycler

import com.example.chat.Message
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.holders.MessageUi
import java.util.ArrayList

fun messageToUi(messages: List<Message>): List<ViewTyped> {
    val messageUis: ArrayList<MessageUi> = ArrayList()
    for (message in messages) {
        messageUis.add(MessageUi(
                message.messageId,
                message.text,
                message.author,
                message.authorId,
                message.avatarUrl,
                message.reactions,
                message.date
        ))
    }
    return messageUis
}
