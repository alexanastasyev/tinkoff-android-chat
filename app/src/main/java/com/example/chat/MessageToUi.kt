package com.example.chat

import java.util.ArrayList

class MessageToUi : (List<Message>) -> List<ViewTyped> {

    override fun invoke(messages: List<Message>): List<ViewTyped> {
        var messageUis: ArrayList<MessageUi> = ArrayList()
        for (message in messages) {
            messageUis.add(MessageUi(
                    message.authorId,
                    message.text,
                    message.author,
                    message.avatarUrl,
                    message.reactions,
                    message.messageId
            ))
        }
        return messageUis
    }
}