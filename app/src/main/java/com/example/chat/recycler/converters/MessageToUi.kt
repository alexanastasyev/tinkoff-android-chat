package com.example.chat.recycler.converters

import com.example.chat.activities.ChatActivity
import com.example.chat.entities.Emoji
import com.example.chat.entities.Message
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.uis.MessageUi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

fun convertMessageToUi(messages: List<Message>): List<ViewTyped> {
    val resultObservable = Observable.fromIterable(messages)
        .subscribeOn(Schedulers.newThread())
        .map { message ->
            MessageUi(
                messageId = message.messageId,
                text = message.text,
                author = message.author,
                authorId = message.authorId,
                avatarUrl = message.avatarUrl,
                reactions = message.reactions.map {
                    Pair(
                        it.emoji,
                        it.amount
                    )
                } as ArrayList<Pair<Emoji, Int>>,
                isEmojiSelected = message.reactions.map { it.reactedUsersId.contains(ChatActivity.THIS_USER_ID) },
                date = SimpleDateFormat("d MMM", Locale.getDefault()).format(message.date)
            )
        }
    return resultObservable.toList().blockingGet()
}
