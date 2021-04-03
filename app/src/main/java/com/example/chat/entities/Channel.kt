package com.example.chat.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Channel (

    @SerialName("name")
    val name: String,

    @SerialName("stream_id")
    val id: Int
)