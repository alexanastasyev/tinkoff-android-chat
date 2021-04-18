package com.example.chat.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.chat.database.converters.DateConverter
import com.example.chat.database.converters.ReactionsConverter
import com.example.chat.database.daos.ChannelDao
import com.example.chat.database.daos.MessageDao
import com.example.chat.database.daos.TopicDao
import com.example.chat.entities.Channel
import com.example.chat.entities.Message
import com.example.chat.entities.Topic

@Database(entities = [
    Channel::class,
    Topic::class,
    Message::class
], version = 11, exportSchema = false)
@TypeConverters(
    DateConverter::class,
    ReactionsConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun topicDao(): TopicDao
    abstract fun messageDao(): MessageDao
}