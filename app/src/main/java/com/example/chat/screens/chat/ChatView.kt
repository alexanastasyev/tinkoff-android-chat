package com.example.chat.screens.chat

import com.example.chat.entities.Message
import com.example.chat.views.EmojiView
import com.example.chat.views.MessageViewGroup

interface ChatView {
    fun addMessages(newMessages: List<Message>, addToTop: Boolean = false)
    fun addSentMessage(message: Message)
    fun removeAllMessages()
    fun createNewEmojiReaction(messageViewGroup: MessageViewGroup, emojiView: EmojiView)
    fun addEmojiReaction(messageViewGroup: MessageViewGroup, emojiView: EmojiView)
    fun removeEmojiReaction(messageViewGroup: MessageViewGroup, emojiView: EmojiView)
    fun showError(errorMessage: String)
}