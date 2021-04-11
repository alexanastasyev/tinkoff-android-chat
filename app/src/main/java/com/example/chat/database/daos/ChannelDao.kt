package com.example.chat.database.daos

import androidx.room.*
import com.example.chat.entities.Channel

@Dao
interface ChannelDao {
    @Query("SELECT * FROM channels")
    fun getAll(): List<Channel>

    @Query("SELECT * FROM channels WHERE isSubscribed = 1")
    fun getSubscribed(): List<Channel>

    @Query("SELECT * FROM channels WHERE id = :id")
    fun getById(id: Int): Channel

    @Query("SELECT * FROM channels WHERE name = :name")
    fun getByName(name: String): Channel

    @Insert
    fun insert(vararg channels: Channel)

    @Delete
    fun delete(channel: Channel)
}