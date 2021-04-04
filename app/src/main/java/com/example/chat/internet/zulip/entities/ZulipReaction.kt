package com.example.chat.internet.zulip.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ZulipReaction(
    @SerialName("emoji_code")
    val emojiCode: String,

    @SerialName("user_id")
    val userId: Int
)