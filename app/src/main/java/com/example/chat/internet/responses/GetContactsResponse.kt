package com.example.chat.internet.responses

import com.example.chat.entities.Contact
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetContactsResponse(

    @SerialName("members")
    val contacts: List<Contact>
)