package com.example.chat.recycler

interface ViewTyped {
    val viewType: Int
        get() = error("provide viewType $this")
}