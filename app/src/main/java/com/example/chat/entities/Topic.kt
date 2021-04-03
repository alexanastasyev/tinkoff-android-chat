package com.example.chat.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Topic(

    @SerialName("name")
    val name: String
)