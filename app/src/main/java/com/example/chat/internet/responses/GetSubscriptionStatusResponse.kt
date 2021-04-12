package com.example.chat.internet.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetSubscriptionStatusResponse(
    @SerialName("is_subscribed")
    val isSubscribed: Boolean
)