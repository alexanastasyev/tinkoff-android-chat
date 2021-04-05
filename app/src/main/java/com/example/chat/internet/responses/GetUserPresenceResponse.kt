package com.example.chat.internet.responses

import com.example.chat.internet.zulip.entities.UserPresence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetUserPresenceResponse(
    @SerialName("presence")
    val presence: UserPresence
)