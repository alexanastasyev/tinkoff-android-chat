package com.example.chat.entities

import androidx.room.Entity
import java.io.Serializable

class Reaction (
    val emoji: Emoji,
    var amount: Int,
    val reactedUsersId: ArrayList<Long>
) : Serializable