package com.example.chat

import com.example.chat.activities.ChatActivity
import com.example.chat.entities.Emoji
import com.example.chat.entities.Message
import com.example.chat.entities.Reaction
import java.util.*

object MessageRepository {
    fun getMessagesList(): List<Message> {
        return listOf(
                Message("Hello, world!", "John Smith", Date(10000), 1, 1),

                Message("Look\n" +
                        "If you had\n" +
                        "One shot\n" +
                        "Or one opportunity\n" +
                        "To seize everything you ever wanted\n" +
                        "In one moment\n" +
                        "Would you capture it\n" +
                        "Or just let it slip?", "Marshall Bruce Mathers III", Date(20000), 2, 2,
                        reactions = arrayListOf(
                                Reaction(Emoji.FACE_IN_LOVE, 3, arrayListOf(1, 3, ChatActivity.THIS_USER_ID)),
                                Reaction(Emoji.FACE_WITH_SUNGLASSES, 3, arrayListOf(1, 2, 3)),
                                Reaction(Emoji.FACE_SMILING, 4, arrayListOf(1, 2, 3, 4))
                        )),

                Message("Nice text, bro", "Dr Dre", Date(30000), 3, 3),

                Message("I agree", "Alexey Anastasyev", Date(40000), ChatActivity.THIS_USER_ID, 4),

                Message("Wanna apple?", "Steve Jobs", Date(700_000_000_000), 4, 5,
                        avatarUrl = "https://cdn.vox-cdn.com/thumbor/gD8CFUq4EEdI8ux04KyGMmuIgcA=/0x86:706x557/920x613/filters:focal(0x86:706x557):format(webp)/cdn.vox-cdn.com/imported_assets/847184/stevejobs.png"),
                Message("When something is important enough, you do it even if the odds are not in your favor.", "Elon Musk", Date(701_000_000_000), 5, 6,
                        avatarUrl = "https://upload.wikimedia.org/wikipedia/commons/8/85/Elon_Musk_Royal_Society_%28crop1%29.jpg")
        )
    }
}