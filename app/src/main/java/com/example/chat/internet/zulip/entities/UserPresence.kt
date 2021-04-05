package com.example.chat.internet.zulip.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UserPresence(
    @SerialName("aggregated")
    val userPresenceAggregated: UserPresenceAggregated
)