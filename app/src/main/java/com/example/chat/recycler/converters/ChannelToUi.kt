package com.example.chat.recycler.converters

import com.example.chat.entities.Channel
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.holders.ChannelUi

fun channelToUi(channels: List<Channel>) : List<ViewTyped> {

    val channelsUis: ArrayList<ChannelUi> = ArrayList()
    for (channel in channels) {
        channelsUis.add(ChannelUi(
                channel.name
        ))
    }
    return channelsUis
}