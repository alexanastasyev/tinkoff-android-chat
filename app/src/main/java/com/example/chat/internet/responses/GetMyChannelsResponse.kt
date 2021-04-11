package com.example.chat.internet.responses

import com.example.chat.entities.Channel
import com.example.chat.internet.zulip.entities.ZulipChannel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetMyChannelsResponse (

    @SerialName("subscriptions")
    val channels: List<ZulipChannel>
)