package com.example.chat.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.chat.entities.Message

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY date")
    fun getAll(): List<Message>

    @Query("SELECT * FROM messages WHERE messageId = :id")
    fun getById(id: Int): Message

    @Query("SELECT * FROM messages WHERE channelName = :channelName AND topicName = :topicName")
    fun getByChannelAndTopic(channelName: String, topicName: String): List<Message>

    @Insert
    fun insert(vararg messages: Message)

    @Delete
    fun delete(message: Message)
}