package com.example.chat.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Contact(

    @SerialName("full_name")
    val name: String,

    @SerialName("avatar_url")
    val imageUrl: String?,

    @SerialName("is_active")
    val isOnline: Boolean = false,

    @SerialName("user_id")
    val id: Int
)