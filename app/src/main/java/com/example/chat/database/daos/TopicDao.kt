package com.example.chat.database.daos

import androidx.room.*
import com.example.chat.entities.Topic

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics")
    fun getAll(): List<Topic>

    @Query("SELECT * FROM topics WHERE channelId = :channelId")
    fun getByChannelId(channelId: Int): List<Topic>

    @Query("SELECT * FROM topics WHERE name = :name AND channelId = :channelId")
    fun getByNameAndChannelId(name: String, channelId: Int): Topic

    @Insert
    fun insert(vararg topics: Topic)

    @Delete
    fun delete(topic: Topic)
}