package com.example.chat.internet

import com.example.chat.entities.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import org.json.JSONObject
import java.util.*

object ZulipService {

    fun getAllChannels(): List<Channel>? {
        val zulipService = RetrofitZulipService.getInstance()
        val response = zulipService.getChannels().execute().body()
        return response?.channels?.map {
            Channel(it.id, it.name, false)
        }
    }

    fun getMyChannels(): List<Channel>? {
        val zulipService = RetrofitZulipService.getInstance()
        val response = zulipService.getMyChannels().execute().body()
        return response?.channels?.map {
            Channel(it.id, it.name, true)
        }
    }

    fun getTopics(channelId: Int): List<Topic>? {
        val zulipService = RetrofitZulipService.getInstance()
        val response = zulipService.getTopics(channelId).execute().body()
        return response?.topics?.map {
            Topic(it.name, channelId)
        }
    }

    fun getContacts(): List<Contact>? {
        val zulipService = RetrofitZulipService.getInstance()
        val response = zulipService.getContacts().execute().body()
        val contactsList = response?.contacts
        if (contactsList != null) {
            for (contact in contactsList) {
                val status = getUserStatus((contact.id))
                contact.status = status
            }
        }
        return contactsList
    }

    private fun getUserStatus(userId: Int): Status {
        val zulipService = RetrofitZulipService.getInstance()
        val response = zulipService.getUserPresence(userId).execute().body()
        val presence = response?.presence?.userPresenceAggregated?.status
        val lastActive = response?.presence?.userPresenceAggregated?.lastActiveTimeInSeconds
        if (lastActive != null) {
            val currentTime = System.currentTimeMillis() / 1000
            if (currentTime - lastActive > 300) {
                return Status.OFFLINE
            }
        }
        return when (presence) {
            "active" -> Status.ACTIVE
            "idle" -> Status.IDLE
            else -> Status.OFFLINE
        }
    }

    fun getProfileDetails(): Contact? {
        val zulipService = RetrofitZulipService.getInstance()
        val response = zulipService.getMyProfileDetails().execute().body()
        if (response != null) {
            val status = getUserStatus(response.id)
            response.status = status
        }
        return response
    }

    fun getMessages(topicName: String, channelName: String): List<Message> {
        val zulipService = RetrofitZulipService.getInstance()
        val response = zulipService.getMessages("newest", 1000, 0,
            """[{"operator":"stream","operand":"${channelName.substring(1)}"}]""").execute().body()

        val messages = arrayListOf<Message>()
        if (response != null) {
            for (zulipMessage in response.messages) {
                if (zulipMessage.topic == topicName) {
                    val newMessage = Message(
                        text = zulipMessage.text,
                        author = zulipMessage.author,
                        date = Date(zulipMessage.dateInSeconds * 1000L),
                        authorId = zulipMessage.authorId.toLong(),
                        messageId = zulipMessage.messageId.toLong(),
                        avatarUrl = zulipMessage.avatarUrl,
                        reactions = arrayListOf()
                    )
                    val reactions = arrayListOf<Reaction>()
                    for (zulipReaction in zulipMessage.reactions) {
                        if (reactions.map { it.emoji.unicode }
                                .contains(Integer.decode("0x${zulipReaction.emojiCode}"))) {
                            for (i in reactions.indices) {
                                if (reactions[i].emoji.unicode == Integer.decode("0x${zulipReaction.emojiCode}")) {
                                    reactions[i].amount += 1
                                    reactions[i].reactedUsersId.add(zulipReaction.userId.toLong())
                                }
                            }
                        } else {
                            reactions.add(
                                Reaction(
                                    Emoji(Integer.decode("0x${zulipReaction.emojiCode}")),
                                    1,
                                    arrayListOf(zulipReaction.userId.toLong())
                                )
                            )
                        }
                    }
                    newMessage.reactions = reactions
                    messages.add(newMessage)
                }
            }
        }
        return messages
    }

    fun sendMessage(channelName: String, topicName: String, messageText: String): Int {
        val zulipService = RetrofitZulipService.getInstance()
        val response = zulipService.sendMessage("stream", channelName, messageText, topicName).execute().body()
        return if (response?.result == "success") {
            response.messageId
        } else {
            -1
        }
    }

    fun addReaction(messageId: Int, emoji: Emoji): Boolean {
        val zulipService = RetrofitZulipService.getInstance()
        val response = zulipService.addReaction(messageId, Emoji.getEmojiNameByUnicode(emoji.unicode), Integer.toHexString(emoji.unicode), "unicode_emoji").execute().body()
        return response?.result == "success"
    }

    fun removeReaction(messageId: Int, emoji: Emoji): Boolean {
        val zulipService = RetrofitZulipService.getInstance()
        val emojiName = Emoji.getEmojiNameByUnicode(emoji.unicode)
        val unicodeString = Integer.toHexString(emoji.unicode)
        val response = zulipService.removeReaction(messageId, emojiName, unicodeString, "unicode_emoji").execute().body()
        return response?.result == "success"
    }
}