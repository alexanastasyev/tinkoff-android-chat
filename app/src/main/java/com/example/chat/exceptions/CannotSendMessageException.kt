package com.example.chat.exceptions

import java.lang.Exception

class CannotSendMessageException : Exception() {
    override val message: String
        get() = super.message + " Cannot send message."
}