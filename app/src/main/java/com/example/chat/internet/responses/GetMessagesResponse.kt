package com.example.chat.internet.responses

import com.example.chat.internet.zulip.entities.ZulipMessage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetMessagesResponse (
    @SerialName("messages")
    val messages: List<ZulipMessage>
)