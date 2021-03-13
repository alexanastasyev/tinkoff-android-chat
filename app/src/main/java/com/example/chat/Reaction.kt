package com.example.chat

class Reaction (
    val emoji: Emoji,
    val amount: Int,
    val reactedUsersId: List<Long>
        )