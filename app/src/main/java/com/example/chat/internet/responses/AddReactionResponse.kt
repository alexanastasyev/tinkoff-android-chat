package com.example.chat.internet.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AddReactionResponse(
    @SerialName("result")
    val result: String
)