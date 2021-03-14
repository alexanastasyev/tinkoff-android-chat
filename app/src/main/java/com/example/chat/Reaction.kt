package com.example.chat

import java.io.Serializable

class Reaction (
        val emoji: Emoji,
        var amount: Int,
        val reactedUsersId: ArrayList<Long>
        ) : Serializable