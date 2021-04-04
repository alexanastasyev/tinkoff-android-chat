package com.example.chat.internet.zulip.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ZulipMessage(
    @SerialName("avatar_url")
    val avatarUrl: String,

    @SerialName("sender_full_name")
    val author: String,

    @SerialName("sender_id")
    val authorId: Int,

    @SerialName("content")
    val text: String,

    @SerialName("id")
    val messageId: Int,

    @SerialName("subject")
    val topic: String,

    @SerialName("timestamp")
    val dateInSeconds: Int,

    @SerialName("reactions")
    val reactions: List<ZulipReaction>
)