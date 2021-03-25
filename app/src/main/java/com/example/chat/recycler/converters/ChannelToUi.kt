package com.example.chat.recycler.converters

import com.example.chat.entities.Channel
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.uis.ChannelUi

fun channelToUi(channels: List<Channel>) : List<ViewTyped> {
    return channels.map { ChannelUi(
        "#${it.name}"
    ) }
}