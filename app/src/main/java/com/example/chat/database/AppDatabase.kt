package com.example.chat.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.chat.database.daos.ChannelDao
import com.example.chat.database.daos.TopicDao
import com.example.chat.entities.Channel
import com.example.chat.entities.Topic

@Database(entities = [
    Channel::class,
    Topic::class
], version = 9, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun topicDao(): TopicDao
}