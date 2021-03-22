package com.example.chat.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.PagerAdapter
import com.example.chat.R
import com.example.chat.entities.Channel
import com.example.chat.entities.Topic
import com.example.chat.recycler.Adapter
import com.example.chat.recycler.ChatHolderFactory
import com.example.chat.recycler.ViewTyped
import com.example.chat.recycler.converters.channelToUi
import com.example.chat.recycler.converters.topicToUi
import com.example.chat.recycler.holders.ChannelUi
import com.example.chat.recycler.holders.TopicUi

class ChannelsListFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_channels_list, container, false)
    }
}