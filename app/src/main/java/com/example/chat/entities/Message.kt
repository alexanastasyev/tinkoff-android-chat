package com.example.chat.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "messages")
class Message(
    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "author")
    val author: String,

    @ColumnInfo(name = "date")
    val date: Date,

    @ColumnInfo(name = "authorId")
    val authorId: Long,

    @PrimaryKey(autoGenerate = false)
    var messageId: Long,

    @ColumnInfo(name = "avatarUrl")
    val avatarUrl: String,

    @ColumnInfo(name = "reactions")
    var reactions: ArrayList<Reaction> = arrayListOf(),

    @ColumnInfo(name = "channelName")
    val channelName: String,

    @ColumnInfo(name = "topicName")
    val topicName: String
) : Serializable