package com.example.chat.internet.zulip.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ZulipChannel (

    @SerialName("stream_id")
    val id: Int,

    @SerialName("name")
    val name: String,

)