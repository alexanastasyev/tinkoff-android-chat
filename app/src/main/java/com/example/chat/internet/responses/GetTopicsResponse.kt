package com.example.chat.internet.responses

import com.example.chat.entities.Topic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetTopicsResponse (

    @SerialName("topics")
    val topics: List<Topic>
)