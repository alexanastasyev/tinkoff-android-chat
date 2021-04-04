package com.example.chat.internet.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RemoveReactionResponse(
    @SerialName("result")
    val result: String
)