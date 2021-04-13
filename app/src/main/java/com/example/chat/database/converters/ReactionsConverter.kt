package com.example.chat.database.converters

import androidx.room.TypeConverter
import com.example.chat.entities.Reaction

class ReactionsConverter {
    @TypeConverter
    fun reactionsToDatabase(reactions: ArrayList<Reaction>): Int {
        return 0
    }

    @TypeConverter
    fun databaseToReactions(value: Int): ArrayList<Reaction> {
        return ArrayList()
    }
}