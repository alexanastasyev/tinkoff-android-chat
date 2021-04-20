package com.example.chat.internet.responses

import com.example.chat.internet.zulip.entities.ZulipTopic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetTopicsResponse (

    @SerialName("topics")
    val topics: List<ZulipTopic>
)