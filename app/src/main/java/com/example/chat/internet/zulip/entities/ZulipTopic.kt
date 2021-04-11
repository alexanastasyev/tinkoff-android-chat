package com.example.chat.internet.zulip.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ZulipTopic(

    @SerialName("name")
    val name: String
)