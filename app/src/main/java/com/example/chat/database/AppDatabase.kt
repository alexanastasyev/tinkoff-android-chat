package com.example.chat.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.chat.database.daos.ChannelDao
import com.example.chat.entities.Channel

@Database(entities = [
    Channel::class
], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
}