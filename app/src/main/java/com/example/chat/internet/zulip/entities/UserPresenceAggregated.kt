package com.example.chat.internet.zulip.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UserPresenceAggregated(
    @SerialName("status")
    val status: String,

    @SerialName("timestamp")
    val lastActiveTimeInSeconds: Int
)