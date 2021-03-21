package com.example.chat.recycler

import com.example.chat.Channel
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