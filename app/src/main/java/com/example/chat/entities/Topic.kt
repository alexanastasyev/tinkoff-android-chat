package com.example.chat.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics", primaryKeys = ["name", "channelId"])
class Topic(

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "channelId")
    val channelId: Int
)