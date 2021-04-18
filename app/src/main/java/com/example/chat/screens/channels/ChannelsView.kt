package com.example.chat.screens.channels

import com.example.chat.entities.Channel

interface ChannelsView {
    fun updateMyChannels(channels: List<Channel>)
    fun updateAllChannels(channels: List<Channel>)
    fun showErrorOfLoading()
    fun showErrorOfSaving()
}